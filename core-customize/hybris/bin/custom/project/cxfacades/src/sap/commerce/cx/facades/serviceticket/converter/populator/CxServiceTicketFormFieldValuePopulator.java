package sap.commerce.cx.facades.serviceticket.converter.populator;

import org.apache.commons.collections4.CollectionUtils;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxServiceTicketFormFieldModel;
import sap.commerce.cx.core.model.CxServiceTicketFormFieldValueModel;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldValueData;

/**
 * Populates {@link CxServiceTicketFormFieldValueData} from {@link CxServiceTicketFormFieldValueModel}.
 */
public class CxServiceTicketFormFieldValuePopulator
		implements Populator<CxServiceTicketFormFieldValueModel, CxServiceTicketFormFieldValueData> {

	private final Converter<CxServiceTicketFormFieldModel, CxServiceTicketFormFieldData> cxServiceTicketFormFieldConverter;

	public CxServiceTicketFormFieldValuePopulator(
			final Converter<CxServiceTicketFormFieldModel, CxServiceTicketFormFieldData> cxServiceTicketFormFieldConverter) {
		this.cxServiceTicketFormFieldConverter = cxServiceTicketFormFieldConverter;
	}

	/**
	 * Copies value metadata including conditional child fields.
	 *
	 * @param source source model
	 * @param target target data object
	 * @throws ConversionException if conversion fails
	 */
	@Override
	public void populate(final CxServiceTicketFormFieldValueModel source,
			final CxServiceTicketFormFieldValueData target) throws ConversionException {
		target.setId(source.getId());
		target.setLabel(source.getLabel());
		if (CollectionUtils.isNotEmpty(source.getChildFields())) {
			target.setChildFields(cxServiceTicketFormFieldConverter.convertAll(source.getChildFields()));
		}
	}
}
