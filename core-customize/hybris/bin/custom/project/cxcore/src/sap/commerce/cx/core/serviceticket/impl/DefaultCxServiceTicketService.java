package sap.commerce.cx.core.serviceticket.impl;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.EMAIL_SERVICE_TICKET_SUBJECT_PREFIX;
import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.EMAIL_SERVICE_TICKET_TEMPLATE;
import static sap.commerce.cx.core.util.CxUtils.getLocalizedString;
import static sap.commerce.cx.core.util.CxUtils.safeLowerCase;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail2.jakarta.HtmlEmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import sap.commerce.cx.core.email.CxEmailService;
import sap.commerce.cx.core.enums.CxServiceTicketStatus;
import sap.commerce.cx.core.enums.CxServiceTicketType;
import sap.commerce.cx.core.model.CxServiceTicketModel;
import sap.commerce.cx.core.serviceticket.CxServiceTicketService;
import sap.commerce.cx.core.serviceticket.dao.CxServiceTicketDAO;
import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;

/**
 * Default implementation of {@link CxServiceTicketService} for ticket retrieval and creation.
 */
public class DefaultCxServiceTicketService implements CxServiceTicketService {
	public static final String UPDATE_SOLR_INDEX_CRONJOB_CODE = "cxdata.import.defaults.solr.updateIndexCronJob";
	private static final String SUBJECT_KEY_PREFIX = "service.servicerequest.request.subject.";

	private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private final CxServiceTicketDAO<CxServiceTicketModel> cxServiceTicketDAO;
	private final ModelService modelService;
	private final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;
	private final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private final EnumerationService enumerationService;
	private final ProductService productService;
	private final KeyGenerator cxServiceTicketCodeGenerator;
	private final CronJobService cronJobService;
	private final ConfigurationService configurationService;
	private final CxEmailService cxEmailService;
	/**
	 * Constructor for dependency injection
	 *
	 * @param cxServiceTicketDAO           {@link CxServiceTicketDAO} the DAO to find tickets
	 * @param modelService                  {@link ModelService} to create new instances
	 * @param b2BCustomerService            {@link B2BCustomerService} to retrieve the current {@link B2BCustomerModel}
	 * @param b2bUnitService                b2bUnitService to retrieve parent account group of the customer
	 * @param enumerationService            {@link EnumerationService} to look up the {@link CxServiceTicketStatus}
	 * @param cxServiceTicketCodeGenerator {@link KeyGenerator} to generate a code for created service ticket.
	 * @param cronJobService                {@link CronJobService} to trigger solr update job
	 * @param configurationService           {@link ConfigurationService} to access configuration properties
	 * @param cxEmailService                {@link CxEmailService} to send emails related to service tickets
	 */

	public DefaultCxServiceTicketService(
			final CxServiceTicketDAO<CxServiceTicketModel> cxServiceTicketDAO,
			final ModelService modelService,
			final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService,
			final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService,
			final EnumerationService enumerationService,
			final ProductService productService,
			final KeyGenerator cxServiceTicketCodeGenerator,
			final CronJobService cronJobService,
			final ConfigurationService configurationService,
			final CxEmailService cxEmailService) {
		this.cxServiceTicketDAO = requireNonNull(cxServiceTicketDAO);
		this.modelService = requireNonNull(modelService);
		this.b2BCustomerService = requireNonNull(b2BCustomerService);
		this.b2bUnitService = requireNonNull(b2bUnitService);
		this.enumerationService = requireNonNull(enumerationService);
		this.productService = requireNonNull(productService);
		this.cxServiceTicketCodeGenerator = requireNonNull(cxServiceTicketCodeGenerator);
		this.cronJobService = requireNonNull(cronJobService);
		this.configurationService = requireNonNull(configurationService);
		this.cxEmailService = requireNonNull(cxEmailService);
	}

	/**
	 * Returns all service tickets.
	 *
	 * @return all tickets
	 */
	@Override
	public List<CxServiceTicketModel> getAllTickets() {
		return cxServiceTicketDAO.getAllTickets();
	}

	/**
	 * Returns tickets that are linked to the given machine code.
	 *
	 * @param machineCode the machine code
	 * @return matching tickets
	 */
	@Override
	public List<CxServiceTicketModel> getAllTicketsWithMachineCode(final String machineCode) {
		return cxServiceTicketDAO.getAllTicketsWithMachineCode(machineCode);
	}

	/**
	 * Returns a ticket by its code.
	 *
	 * @param code the ticket code
	 * @return the matching ticket or {@code null}
	 */
	@Override
	public CxServiceTicketModel getTicketForCode(final String code) {
		return cxServiceTicketDAO.findByCode(code).orElse(null);
	}

	/**
	 * Creates and stores a new service ticket from a submitted form.
	 *
	 * @param formData the submitted form data
	 * @return the created ticket
	 * @throws ModelSavingException   if persisting the model fails
	 * @throws ServiceTicketException if ticket setup or follow-up processing fails
	 */
	@Override
	public CxServiceTicketModel createServiceTicket(final CxServiceTicketFormData formData)
			throws ModelSavingException, ServiceTicketException {
		try {
			final B2BCustomerModel currentB2BCustomer = b2BCustomerService.getCurrentB2BCustomer();
			final CxServiceTicketModel ticket = createTicket(formData, currentB2BCustomer);

			modelService.save(ticket);
			triggerSolrUpdateJob();

			return ticket;
		} catch (final ModelSavingException e) {
			throw new ServiceTicketException(e, "Could not persist service ticket.", "ticketCreationError");
		} catch (final Exception e) {
			throw new ServiceTicketException(e, "Could not create service ticket.", "ticketCreationError");
		}
	}

	@Override
	public void sendServiceTicketEmail(Map<String, Object> contextParameters, CxServiceTicketData ticket) {
		// Example:
		// cx.email.serviceticket.subject.technical_support= Request Technical Support. Ticket ID {0}
		final String subject = getLocalizedString(
				EMAIL_SERVICE_TICKET_SUBJECT_PREFIX,
				safeLowerCase(ticket.getTypeCode()),
				ticket.getCode());

		final String template = configurationService.getConfiguration().getString(EMAIL_SERVICE_TICKET_TEMPLATE);

		final HtmlEmail email = cxEmailService.prepareMail(
				template,
				subject,
				contextParameters,
				ticket.getRecipientsTo(),
				ticket.getRecipientsCc());

		cxEmailService.sendMail(email);
	}

	private CxServiceTicketModel createTicket(CxServiceTicketFormData formData, B2BCustomerModel currentB2BCustomer) {
		final CxServiceTicketModel ticket = modelService.create(CxServiceTicketModel.class);

		ticket.setCode(cxServiceTicketCodeGenerator.generate().toString());
		ticket.setStatus(enumerationService.getEnumerationValue(CxServiceTicketStatus.class, CxServiceTicketStatus.valueOf("OPEN").getCode()));
		ticket.setTitle(formData.getTitle());
		ticket.setDescription(formData.getDescription());
		ticket.setMachines(getMachines(formData.getMachines()));
		ticket.setCustomer(currentB2BCustomer);
		ticket.setAllMachines(false);
		ticket.setType(getServiceTicketType(formData));
		ticket.setParentAccountGroup(b2bUnitService.getParent(currentB2BCustomer));
		ticket.setForm(createRawMessageJson(formData));
		if (CollectionUtils.isNotEmpty(formData.getRecipients())) {
			ticket.setRecipientsTo(formData.getRecipients());
		}
		final String customerEmail = currentB2BCustomer.getEmail();
		if (StringUtils.isNotBlank(customerEmail)) {
			ticket.setRecipientsCc(Collections.singleton(customerEmail));
		}
		return ticket;
	}

	private void triggerSolrUpdateJob() throws ServiceTicketException {
		try {
			final String updateSolrCronJob = configurationService.getConfiguration().getString(UPDATE_SOLR_INDEX_CRONJOB_CODE);
			final CronJobModel cronJob = cronJobService.getCronJob(updateSolrCronJob);
			cronJobService.performCronJob(cronJob, true);
		} catch (final Exception e) {
			throw new ServiceTicketException(
					e,
					String.format("Could not start cron job for parameters [code=%s]! Please verify the cron job is configured correctly.",
							UPDATE_SOLR_INDEX_CRONJOB_CODE),
					"ticketCreationError");
		}
	}

	private Set<ProductModel> getMachines(final List<String> machines) {
		return emptyIfNull(machines).stream()
				.filter(StringUtils::isNotBlank)
				.map(productService::getProductForCode)
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(HashSet::new));
	}

	private String createRawMessageJson(final CxServiceTicketFormData formData) {
		try {
			return objectWriter.writeValueAsString(formData.getFormFields());
		} catch (final JsonProcessingException e) {
			return StringUtils.EMPTY;
		}
	}

	private CxServiceTicketType getServiceTicketType(final CxServiceTicketFormData formData) {
		if (StringUtils.isBlank(formData.getType())) {
			return null;
		}
		return enumerationService.getEnumerationValue(CxServiceTicketType.class, formData.getType());
	}
}
