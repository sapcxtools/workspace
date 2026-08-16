package sap.commerce.cx.facades.serviceticket.converter.populator;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import sap.commerce.cx.core.enums.CxServiceTicketStatus;
import sap.commerce.cx.core.enums.CxServiceTicketType;
import sap.commerce.cx.core.model.CxServiceTicketModel;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;

/**
 * Populates {@link CxServiceTicketData} from {@link CxServiceTicketModel}.
 */
public class CxServiceTicketPopulator implements Populator<CxServiceTicketModel, CxServiceTicketData> {
	private final Converter<ProductModel, ProductData> machineConverter;
	private final Converter<B2BCustomerModel, CustomerData> customerConverter;
	private final Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;
	private final EnumerationService enumerationService;
	private final CommonI18NService commonI18NService;

	public CxServiceTicketPopulator(
			final Converter<ProductModel, ProductData> machineConverter,
			final Converter<B2BCustomerModel, CustomerData> customerConverter,
			final Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter,
			final EnumerationService enumerationService,
			final CommonI18NService commonI18NService) {
		this.machineConverter = requireNonNull(machineConverter);
		this.customerConverter = requireNonNull(customerConverter);
		this.b2BUnitConverter = requireNonNull(b2BUnitConverter);
		this.enumerationService = requireNonNull(enumerationService);
		this.commonI18NService = requireNonNull(commonI18NService);
	}

	/**
	 * Copies all ticket attributes including localized status and type labels.
	 *
	 * @param source source model
	 * @param target target data object
	 */
	@Override
	public void populate(final CxServiceTicketModel source, final CxServiceTicketData target) {
		target.setCode(source.getCode());
		if (source.getType() != null) {
			target.setTypeCode(source.getType().getCode());
			final HybrisEnumValue typeEnumValue = enumerationService.getEnumerationValue(CxServiceTicketType.class, source.getType().getCode());
			final Locale locale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());
			target.setType(enumerationService.getEnumerationName(typeEnumValue, locale));
		}
		target.setTitle(source.getTitle());

		if (source.getStatus() != null) {
			final HybrisEnumValue statusEnumValue = enumerationService.getEnumerationValue(CxServiceTicketStatus.class, source.getStatus().getCode());
			final Locale locale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());
			target.setStatus(enumerationService.getEnumerationName(statusEnumValue, locale));
		}
		target.setDescription(source.getDescription());
		target.setAllMachines(source.getAllMachines());
		if (CollectionUtils.isNotEmpty(source.getMachines())) {
			target.setMachines(machineConverter.convertAll(source.getMachines()));
		}
		if (source.getCustomer() != null) {
			target.setCustomer(customerConverter.convert(source.getCustomer()));
		}
		target.setCreationTime(source.getCreationtime());
		target.setIsNew(source.getIsNew());
		if (source.getParentAccountGroup() != null) {
			target.setParentAccountGroup(b2BUnitConverter.convert(source.getParentAccountGroup()));
		}
		target.setForm(source.getForm());
		if (CollectionUtils.isNotEmpty(source.getRecipientsTo())) {
			target.setRecipientsTo(source.getRecipientsTo().stream().toList());
		}
		if (CollectionUtils.isNotEmpty(source.getRecipientsCc())) {
			target.setRecipientsCc(source.getRecipientsCc().stream().toList());
		}
	}
}
