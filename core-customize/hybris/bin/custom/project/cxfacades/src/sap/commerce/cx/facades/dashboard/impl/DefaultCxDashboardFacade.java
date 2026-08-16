package sap.commerce.cx.facades.dashboard.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import sap.commerce.cx.core.dashboard.service.CxDashboardService;
import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.core.model.CxDashboardWidgetSettingModel;
import sap.commerce.cx.facades.dashboard.CxDashboardFacade;
import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetSettingData;

/**
 * Default facade implementation for dashboard configuration handling.
 * <p>
 * This implementation delegates persistence-related operations to the dashboard
 * service and handles data-to-model conversion as well as mapping between dashboard
 * configurations and dashboard widget configurations.
 * </p>
 */
public class DefaultCxDashboardFacade implements CxDashboardFacade {

	private final CxDashboardService dashboardService;
	private final ModelService modelService;
	private final Converter<CxDashboardConfigModel, CxDashboardConfigData> dashboardConfigConverter;
	private final Converter<CxDashboardWidgetModel, CxDashboardWidgetData> dashboardWidgetConverter;
	public DefaultCxDashboardFacade(final CxDashboardService dashboardService, ModelService modelService,
			final Converter<CxDashboardConfigModel, CxDashboardConfigData> dashboardConfigConverter,
			Converter<CxDashboardWidgetModel, CxDashboardWidgetData> dashboardWidgetConverter) {
		this.dashboardService = dashboardService;
		this.modelService = modelService;
		this.dashboardConfigConverter = dashboardConfigConverter;
		this.dashboardWidgetConverter = dashboardWidgetConverter;
	}

	@Override
	public CxDashboardConfigData getDefaultDashboardConfig() {
		return dashboardConfigConverter.convert(dashboardService.getDefaultDashboardConfig());
	}

	/**
	 * Returns all dashboard configurations for the current customer.
	 * <p>
	 * If the customer does not have any dashboard configuration yet, a personal copy
	 * of the default configuration is created first and returned as the only entry.
	 * </p>
	 *
	 * @return a list of the customer's own dashboard configurations, sorted by position
	 */
	@Override
	public List<CxDashboardConfigData> getDashboardConfigsForCurrentUser() {
		List<CxDashboardConfigModel> configs = dashboardService.getDashboardConfigsForCurrentUser();
		if (configs.isEmpty()) {
			final CxDashboardConfigModel initialConfig = dashboardService.createDashboardConfigFromDefault();
			configs = initialConfig != null ? List.of(initialConfig) : configs;
		}

		final List<CxDashboardConfigData> configDatas = new ArrayList<>(dashboardConfigConverter.convertAll(configs));
		configDatas.sort(Comparator.comparingInt(CxDashboardConfigData::getPosition));
		return configDatas;
	}

	/**
	 * Saves the given dashboard configuration for the current customer.
	 * <p>
	 * If no existing configuration is found for the given code, a new dashboard
	 * configuration is created first. Widget configurations are always recreated
	 * from the given data.
	 * </p>
	 *
	 * @param dashboardConfigData the dashboard configuration data to save
	 * @return the persisted dashboard configuration data
	 */
	@Override
	public CxDashboardConfigData saveDashboardConfigForCurrentUser(final CxDashboardConfigData dashboardConfigData) {
		CxDashboardConfigModel model = dashboardService.getDashboardConfigForCodeAndCurrentUser(dashboardConfigData.getCode());
		if (model == null) {
			model = dashboardService.createNewDashboardConfig();
		}

		dashboardService.removeWidgetConfigs(model);
		mapDashboardConfig(dashboardConfigData, model, dashboardConfigData.getCode());
		dashboardService.saveDashboardConfig(model);
		return dashboardConfigConverter.convert(model);
	}

	protected void mapDashboardConfig(final CxDashboardConfigData source, final CxDashboardConfigModel target, final String dashboardConfigCode) {
		target.setName(source.getName());
		target.setActive(source.isActive());
		target.setPosition(source.getPosition());

		if (source.getWidgetConfigs() != null && !source.getWidgetConfigs().isEmpty()) {
			final List<CxDashboardWidgetConfigModel> createdWidgets = source.getWidgetConfigs().stream()
					.map(sourceWidget -> {
						final CxDashboardWidgetConfigModel newDashboardWidgetConfigModel = dashboardService
								.createNewDashboardWidgetConfig(sourceWidget.getWidget().getCode(), dashboardConfigCode);
						mapDashboardWidgetConfig(sourceWidget, newDashboardWidgetConfigModel);
						return newDashboardWidgetConfigModel;
					})
					.toList();
			target.setWidgetConfigs(createdWidgets);
		}
	}

	protected void mapDashboardWidgetConfig(final CxDashboardWidgetConfigData source, final CxDashboardWidgetConfigModel target) {
		target.setPosition(source.getPosition());
		target.setColumnSpan(source.getColumnSpan());
		target.setRowSpan(source.getRowSpan());
		if (source.getSettings() != null) {
			List<CxDashboardWidgetSettingModel> settings = source.getSettings().stream()
					.map(this::mapDashboardWidgetSettings).toList();
			target.setSettings(settings);
		}
	}

	protected CxDashboardWidgetSettingModel mapDashboardWidgetSettings(final CxDashboardWidgetSettingData source) {
		CxDashboardWidgetSettingModel targetSetting = modelService.create(CxDashboardWidgetSettingModel.class);
		targetSetting.setKey(source.getKey());
		targetSetting.setValue(source.getValue());
		return targetSetting;
	}

	@Override
	public void deleteDashboardConfigForCurrentUser(final String configCode) {
		dashboardService.deleteDashboardConfigForCurrentUser(configCode);
	}

	@Override
	public List<CxDashboardWidgetData> getAllAvailableDashboardWidgets() {
		Collection<CxDashboardWidgetModel> widgetModels = dashboardService.getAllAvailableWidgets();
		return dashboardWidgetConverter.convertAll(widgetModels);
	}
}
