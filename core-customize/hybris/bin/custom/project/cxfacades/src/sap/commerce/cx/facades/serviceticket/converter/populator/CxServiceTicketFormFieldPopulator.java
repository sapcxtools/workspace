package sap.commerce.cx.facades.serviceticket.converter.populator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxServiceTicketFormFieldModel;
import sap.commerce.cx.core.model.CxServiceTicketFormFieldValueModel;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldValueData;

/**
 * Populates {@link CxServiceTicketFormFieldData} from {@link CxServiceTicketFormFieldModel}.
 */
public class CxServiceTicketFormFieldPopulator
		implements Populator<CxServiceTicketFormFieldModel, CxServiceTicketFormFieldData> {
	private final Converter<CxServiceTicketFormFieldValueModel, CxServiceTicketFormFieldValueData> cxServiceTicketFormFieldValueConverter;

	public CxServiceTicketFormFieldPopulator(
			final Converter<CxServiceTicketFormFieldValueModel, CxServiceTicketFormFieldValueData> cxServiceTicketFormFieldValueConverter) {
		this.cxServiceTicketFormFieldValueConverter = cxServiceTicketFormFieldValueConverter;
	}

	/**
	 * Copies all relevant field attributes including selectable values.
	 *
	 * @param source source model
	 * @param target target data object
	 * @throws ConversionException if conversion fails
	 */
	@Override
	public void populate(final CxServiceTicketFormFieldModel source, final CxServiceTicketFormFieldData target)
			throws ConversionException {
		target.setId(source.getId());
		target.setLabel(source.getLabel());
		target.setDescription(source.getDescription());
		target.setHidden(source.isHidden());
		target.setRequired(BooleanUtils.isTrue(source.isRequired()));
		if (source.getFieldType() != null) {
			target.setFieldType(source.getFieldType().getCode());
		}
		target.setDefaultValue(source.getDefaultValue());
		target.setPlaceholder(source.getPlaceholder());
		target.setMinValue(source.getMinValue());
		target.setMaxValue(source.getMaxValue());
		target.setMinLength(source.getMinLength());
		target.setMaxLength(source.getMaxLength());
		if (CollectionUtils.isNotEmpty(source.getFormFieldValues())) {
			target.setFormFieldValues(cxServiceTicketFormFieldValueConverter.convertAll(source.getFormFieldValues()));
		}
	}
}
