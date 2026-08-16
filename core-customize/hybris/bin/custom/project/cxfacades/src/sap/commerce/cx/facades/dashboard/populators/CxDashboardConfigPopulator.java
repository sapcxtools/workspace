package sap.commerce.cx.facades.dashboard.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;

public class CxDashboardConfigPopulator implements Populator<CxDashboardConfigModel, CxDashboardConfigData> {
	private final Converter<CxDashboardWidgetConfigModel, CxDashboardWidgetConfigData> widgetConfigConverter;

	public CxDashboardConfigPopulator(final Converter<CxDashboardWidgetConfigModel, CxDashboardWidgetConfigData> widgetConfigConverter) {
		this.widgetConfigConverter = widgetConfigConverter;
	}

	@Override
	public void populate(final CxDashboardConfigModel source, final CxDashboardConfigData target) throws ConversionException {
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setActive(source.isActive());
		target.setPosition(source.getPosition());
		target.setWidgetConfigs(widgetConfigConverter.convertAll(source.getWidgetConfigs()));
	}
}
