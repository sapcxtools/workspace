package sap.commerce.cx.facades.serviceticket.impl;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.EMAIL_SERVICE_TICKET_INTRO_PREFIX;
import static sap.commerce.cx.core.util.CxUtils.getLocalizedString;
import static sap.commerce.cx.core.util.CxUtils.safeLowerCase;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;
import sap.commerce.cx.core.contentsearch.impl.DefaultContentSearchStrategy;
import sap.commerce.cx.core.enums.CxServiceTicketFormFieldType;
import sap.commerce.cx.core.enums.CxServiceTicketStatus;
import sap.commerce.cx.core.model.CxServiceTicketModel;
import sap.commerce.cx.core.serviceticket.CxServiceTicketService;
import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.CxServiceTicketCountData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldValueData;
import sap.commerce.cx.facades.serviceticket.CxServiceTicketFacade;

/**
 * Default facade implementation for service ticket use cases.
 *
 * @param <STATE>  search state type
 * @param <RESULT> search result type
 */
public class DefaultCxServiceTicketFacade<STATE, RESULT extends ContentSearchPageData<STATE, RESULT>>
		implements CxServiceTicketFacade<STATE, RESULT> {

	private static final String PARAM_INTRO = "intro";
	private static final String PARAM_TICKET_CODE = "ticketCode";
	private static final String PARAM_CUSTOMER_NAME = "customerName";
	private static final String PARAM_CUSTOMER_EMAIL = "customerEmail";
	private static final String PARAM_ACCOUNT_GROUP = "accountGroup";
	private static final String PARAM_ADDRESS = "address";
	private static final String PARAM_MACHINES = "machines";
	private static final String PARAM_FORM_FIELDS = "formFields";

	public static final String CHECKBOXES_CODE = CxServiceTicketFormFieldType.CHECKBOXES.getCode();

	private final CxServiceTicketService cxServiceTicketService;
	private final Converter<CxServiceTicketModel, CxServiceTicketData> cxServiceTicketConverter;
	private final DefaultContentSearchStrategy<STATE, RESULT> cxServiceTicketSearchStrategy;
	private final Converter<ContentSearchPageData<STATE, RESULT>, ContentSearchPageData<STATE, RESULT>> cxTicketSearchPageConverter;

	/**
	 * Constructor for dependency injection
	 *
	 * @param cxServiceTicketService        {@link CxServiceTicketService} to get a ticket for code or create a new one
	 * @param cxServiceTicketConverter      {@link Converter} used to convert {@link CxServiceTicketModel} to {@link CxServiceTicketData}
	 * @param cxServiceTicketSearchStrategy {@link DefaultContentSearchStrategy} {@link CxServiceTicketData}
	 * @param cxTicketSearchPageConverter   {@link Converter} used to convert {@link ContentSearchPageData} to {@link ContentSearchPageData}
	 */
	public DefaultCxServiceTicketFacade(final CxServiceTicketService cxServiceTicketService,
			final Converter<CxServiceTicketModel, CxServiceTicketData> cxServiceTicketConverter,
			final DefaultContentSearchStrategy<STATE, RESULT> cxServiceTicketSearchStrategy,
			final Converter<ContentSearchPageData<STATE, RESULT>, ContentSearchPageData<STATE, RESULT>> cxTicketSearchPageConverter) {
		this.cxServiceTicketService = requireNonNull(cxServiceTicketService);
		this.cxServiceTicketConverter = requireNonNull(cxServiceTicketConverter);
		this.cxServiceTicketSearchStrategy = requireNonNull(cxServiceTicketSearchStrategy);
		this.cxTicketSearchPageConverter = requireNonNull(cxTicketSearchPageConverter);
	}

	@Override
	public List<CxServiceTicketData> getAllTicketsWithMachineCode(final String machineCode) {
		if (StringUtils.isEmpty(machineCode)) {
			return List.of();
		}
		final List<CxServiceTicketModel> tickets = cxServiceTicketService.getAllTicketsWithMachineCode(machineCode);
		if (CollectionUtils.isEmpty(tickets)) {
			return List.of();
		}
		return cxServiceTicketConverter.convertAll(tickets);
	}

	@Override
	public CxServiceTicketData getTicketForCode(final String ticketCode) {
		final CxServiceTicketModel ticketForCode = cxServiceTicketService.getTicketForCode(ticketCode);
		if (ticketForCode != null) {
			return cxServiceTicketConverter.convert(ticketForCode);
		}
		return new CxServiceTicketData();
	}

	@Override
	public ContentSearchPageData<STATE, RESULT> search(final String filters, final String query, final int currentPage,
			final int pageSize, final String sort) {
		final ContentSearchPageData<STATE, RESULT> searchResult = cxServiceTicketSearchStrategy.search(filters, query, currentPage, pageSize, sort);

		return cxTicketSearchPageConverter.convert(searchResult);
	}

	@Override
	public CxServiceTicketData createTicket(final CxServiceTicketFormData formData) throws ServiceTicketException {
		final List<CxServiceTicketFormFieldData> selectedFormFields = collectSelectedFormFields(formData.getFormFields());
		replaceFieldValueIdWithValueLabel(selectedFormFields);
		formData.setFormFields(selectedFormFields);

		return cxServiceTicketConverter.convert(cxServiceTicketService.createServiceTicket(formData));
	}

	@Override
	public void sendServiceTicketEmail(final CxServiceTicketData ticket,
			final List<CxServiceTicketFormFieldData> formFields) {
		final List<CxServiceTicketFormFieldData> notHiddenFormFields = emptyIfNull(formFields).stream()
				.filter(field -> !field.getHidden()).collect(Collectors.toList());

		cxServiceTicketService.sendServiceTicketEmail(buildContextParameters(ticket, notHiddenFormFields), ticket);
	}

	@Override
	public CxServiceTicketCountData getTicketsCounts(final String currentCustomer) {
		final String completedStatus = CxServiceTicketStatus.valueOf("COMPLETED").getCode();
		;

		final Map<Boolean, Long> counts = cxServiceTicketService.getAllTickets().stream()
				.filter(ticket -> ticket.getCustomer() != null)
				.filter(ticket -> currentCustomer.equals(ticket.getCustomer().getUid()))
				.collect(Collectors.partitioningBy(
						ticket -> ticket.getStatus() != null
								&& completedStatus.equalsIgnoreCase(ticket.getStatus().getCode()),
						Collectors.counting()));

		final CxServiceTicketCountData data = new CxServiceTicketCountData();
		data.setClosedRequests(counts.get(true).intValue());
		data.setOpenRequests(counts.get(false).intValue());

		return data;
	}

	/**
	 * Collects fields with user input and recursively includes selected conditional child fields.
	 */
	private List<CxServiceTicketFormFieldData> collectSelectedFormFields(
			final List<CxServiceTicketFormFieldData> formFields) {

		final List<CxServiceTicketFormFieldData> result = new ArrayList<>();
		collectSelectedFormFields(formFields, result);
		return result;
	}

	private void collectSelectedFormFields(final List<CxServiceTicketFormFieldData> formFields,
			final List<CxServiceTicketFormFieldData> result) {
		if (CollectionUtils.isEmpty(formFields)) {
			return;
		}

		for (final CxServiceTicketFormFieldData field : formFields) {
			if (field == null || StringUtils.isBlank(field.getValue())) {
				continue;
			}

			result.add(field);

			final Set<String> selectedValueIds = isCheckboxField(field)
					? parseSelectedValueIds(field.getValue())
					: Collections.emptySet();

			for (final CxServiceTicketFormFieldValueData value : emptyIfNull(field.getFormFieldValues())) {
				if (value == null || CollectionUtils.isEmpty(value.getChildFields())) {
					continue;
				}

				if (isSelected(field, value, selectedValueIds)) {
					collectSelectedFormFields(value.getChildFields(), result);
				}
			}
		}
	}

	private boolean isSelected(final CxServiceTicketFormFieldData field,
			final CxServiceTicketFormFieldValueData value,
			final Set<String> selectedValueIds) {
		final String valueId = StringUtils.trimToEmpty(value.getId());
		if (isCheckboxField(field)) {
			return selectedValueIds.contains(valueId);
		}
		return Objects.equals(StringUtils.trimToEmpty(field.getValue()), valueId);
	}

	/**
	 * Replaces selected value ids with their labels so downstream consumers receive readable values.
	 */
	private void replaceFieldValueIdWithValueLabel(final List<CxServiceTicketFormFieldData> fields) {
		if (CollectionUtils.isEmpty(fields)) {
			return;
		}

		for (final CxServiceTicketFormFieldData field : fields) {
			if (field == null || StringUtils.isBlank(field.getValue())) {
				continue;
			}

			final Map<String, String> labelById = buildLabelByIdMap(emptyIfNull(field.getFormFieldValues()));

			if (isCheckboxField(field)) {
				final String labels = Arrays.stream(field.getValue().split(","))
						.map(String::trim)
						.filter(StringUtils::isNotBlank)
						.map(labelById::get)
						.filter(StringUtils::isNotBlank)
						.collect(Collectors.joining(", "));

				field.setValue(labels);
			} else {
				final String label = labelById.get(StringUtils.trimToEmpty(field.getValue()));
				if (StringUtils.isNotBlank(label)) {
					field.setValue(label);
				}
			}
		}
	}

	private boolean isCheckboxField(final CxServiceTicketFormFieldData field) {
		return Objects.equals(field.getFieldType(), CHECKBOXES_CODE);
	}

	private Set<String> parseSelectedValueIds(final String rawValue) {
		return Arrays.stream(StringUtils.defaultString(rawValue).split(","))
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());
	}

	private Map<String, String> buildLabelByIdMap(final List<CxServiceTicketFormFieldValueData> fieldValues) {
		final Map<String, String> labelById = new LinkedHashMap<>();

		for (final CxServiceTicketFormFieldValueData value : fieldValues) {
			if (value == null || StringUtils.isBlank(value.getId()) || StringUtils.isBlank(value.getLabel())) {
				continue;
			}
			labelById.putIfAbsent(StringUtils.trimToEmpty(value.getId()), value.getLabel());
		}
		return labelById;
	}

	private Map<String, Object> buildContextParameters(final CxServiceTicketData ticketData,
			final List<CxServiceTicketFormFieldData> formFields) {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put(PARAM_INTRO, getLocalizedString(EMAIL_SERVICE_TICKET_INTRO_PREFIX, safeLowerCase(ticketData.getTypeCode())));
		parameters.put(PARAM_TICKET_CODE, ticketData.getCode());
		parameters.put(PARAM_CUSTOMER_NAME, ticketData.getCustomer().getName());
		parameters.put(PARAM_CUSTOMER_EMAIL, ticketData.getCustomer().getEmail());

		if (ticketData.getParentAccountGroup() != null) {
			parameters.put(PARAM_ACCOUNT_GROUP, ticketData.getParentAccountGroup());

			if (CollectionUtils.isNotEmpty(ticketData.getParentAccountGroup().getAddresses())) {
				parameters.put(PARAM_ADDRESS, ticketData.getParentAccountGroup().getAddresses().stream().findFirst().orElse(null));
			}
		}

		if (CollectionUtils.isNotEmpty(ticketData.getMachines())) {
			parameters.put(PARAM_MACHINES, ticketData.getMachines());
		}

		if (CollectionUtils.isNotEmpty(formFields)) {
			parameters.put(PARAM_FORM_FIELDS, formFields);
		}
		return parameters;
	}
}
