package sap.commerce.cx.facades.dashboard.populators;

import java.util.List;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetSettingData;

public class CxDashboardWidgetConfigPopulator implements Populator<CxDashboardWidgetConfigModel, CxDashboardWidgetConfigData> {
	private final Converter<CxDashboardWidgetModel, CxDashboardWidgetData> widgetConverter;

	public CxDashboardWidgetConfigPopulator(Converter<CxDashboardWidgetModel, CxDashboardWidgetData> widgetConverter) {
		this.widgetConverter = widgetConverter;
	}

	@Override
	public void populate(final CxDashboardWidgetConfigModel source, final CxDashboardWidgetConfigData target) throws ConversionException {
		target.setPosition(source.getPosition());
		target.setColumnSpan(source.getColumnSpan());
		target.setRowSpan(source.getRowSpan());
		target.setWidget(widgetConverter.convert(source.getWidget()));
		if (source.getSettings() != null) {
			List<CxDashboardWidgetSettingData> settings = source.getSettings().stream()
					.map(setting -> {
						CxDashboardWidgetSettingData settingData = new CxDashboardWidgetSettingData();
						settingData.setKey(setting.getKey());
						settingData.setValue(setting.getValue());
						return settingData;
					})
					.toList();
			target.setSettings(settings);
		}
	}
}
