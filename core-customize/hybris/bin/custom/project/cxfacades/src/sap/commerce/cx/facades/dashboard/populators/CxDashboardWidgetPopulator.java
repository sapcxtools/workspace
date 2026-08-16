package sap.commerce.cx.facades.dashboard.populators;

import de.hybris.platform.cms2.model.contents.components.CMSFlexComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.I18NService;

import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;

public class CxDashboardWidgetPopulator implements Populator<CxDashboardWidgetModel, CxDashboardWidgetData> {
	private final I18NService i18NService;

	public CxDashboardWidgetPopulator(I18NService i18NService) {
		this.i18NService = i18NService;
	}

	@Override
	public void populate(CxDashboardWidgetModel source, CxDashboardWidgetData target) throws ConversionException {
		target.setCode(source.getCode());
		SimpleCMSComponentModel contentComponent = source.getContentComponent();
		target.setContentComponentUid(contentComponent.getUid());
		if (contentComponent instanceof CMSFlexComponentModel flexComponent) {
			target.setContentComponentType(flexComponent.getFlexType());
		} else {
			target.setContentComponentType(contentComponent.getItemtype());
		}
		target.setMinColumnSpan(source.getMinColumnSpan());
		target.setMinRowSpan(source.getMinRowSpan());
		target.setMultipleOccurrenceAllowed(source.isMultipleOccurrenceAllowed());
		target.setDisplayName(source.getDisplayName(i18NService.getCurrentLocale()));
	}
}
