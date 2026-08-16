package sap.commerce.cx.facades.cms.populator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cxcore.model.cms.CxFormListComponentModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates the {@code forms} attribute of {@link CxFormListComponentModel} into the CMS component OCC response.
 * Hybris OCC does not automatically serialize M:N relations to non-CMS item types,
 * so this populator explicitly maps the related {@code CxServiceTicketForm} IDs into the data map.
 */
public class CxFormListComponentCMSPopulator implements Populator<ItemModel, Map<String, Object>> {

	@Override
	public void populate(final ItemModel source, final Map<String, Object> target) throws ConversionException {
		if (!(source instanceof CxFormListComponentModel)) {
			return;
		}
		final CxFormListComponentModel component = (CxFormListComponentModel) source;
		if (component.getForms() != null && !component.getForms().isEmpty()) {
			final List<String> formIds = component.getForms().stream()
					.map(form -> form.getId())
					.collect(Collectors.toList());
			target.put("forms", formIds);
		}
	}
}
