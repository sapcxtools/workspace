package sap.commerce.cx.facades.serviceticket.converter.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxServiceTicketFormFieldModel;
import sap.commerce.cx.core.model.CxServiceTicketFormModel;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;

/**
 * Populates {@link CxServiceTicketFormData} from {@link CxServiceTicketFormModel}.
 */
public class CxServiceTicketFormPopulator
		implements Populator<CxServiceTicketFormModel, CxServiceTicketFormData> {

	private final Converter<CxServiceTicketFormFieldModel, CxServiceTicketFormFieldData> cxServiceTicketFormFieldConverter;

	public CxServiceTicketFormPopulator(
			final Converter<CxServiceTicketFormFieldModel, CxServiceTicketFormFieldData> cxServiceTicketFormFieldConverter) {
		this.cxServiceTicketFormFieldConverter = cxServiceTicketFormFieldConverter;
	}

	/**
	 * Copies all form metadata and only active form fields.
	 *
	 * @param source source model
	 * @param target target data object
	 */
	@Override
	public void populate(final CxServiceTicketFormModel source, final CxServiceTicketFormData target) {
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		if (source.getType() != null) {
			target.setType(source.getType().getCode());
		}
		target.setDescription(source.getDescription());
		target.setDynamicRecipient(BooleanUtils.isTrue(source.getDynamicRecipient()));
		if (CollectionUtils.isNotEmpty(source.getRecipients())) {
			target.setRecipients(new ArrayList<>(source.getRecipients()));
		}
		if (CollectionUtils.isNotEmpty(source.getFormFields())) {
			final List<CxServiceTicketFormFieldModel> activeFieldModels = source.getFormFields().stream()
					.filter(CxServiceTicketFormFieldModel::isActive).collect(Collectors.toList());
			target.setFormFields(cxServiceTicketFormFieldConverter.convertAll(activeFieldModels));
		}
	}
}
