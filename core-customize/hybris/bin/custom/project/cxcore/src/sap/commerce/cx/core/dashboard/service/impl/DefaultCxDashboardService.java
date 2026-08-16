package sap.commerce.cx.core.dashboard.service.impl;

import static sap.commerce.cx.core.constants.CxCoreConstants.DASHBOARD.CUSTOM_CONFIG_PATTERN;
import static sap.commerce.cx.core.constants.CxCoreConstants.DASHBOARD.CUSTOM_CONFIG_PREFIX;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import sap.commerce.cx.core.dashboard.dao.CxDashboardDao;
import sap.commerce.cx.core.dashboard.service.CxDashboardService;
import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.core.model.CxDashboardWidgetSettingModel;

/**
 * Default service implementation for managing dashboard configurations.
 */
public class DefaultCxDashboardService implements CxDashboardService {
	private final ModelService modelService;
	private final UserService userService;
	private final CxDashboardDao dashboardDao;
	private final BaseSiteService baseSiteService;

	public DefaultCxDashboardService(final ModelService modelService, final UserService userService, final CxDashboardDao dashboardDao, BaseSiteService baseSiteService) {
		this.modelService = modelService;
		this.userService = userService;
		this.dashboardDao = dashboardDao;
		this.baseSiteService = baseSiteService;
	}

	@Override
	public CxDashboardConfigModel getDefaultDashboardConfig() {
		return dashboardDao.getDefaultDashboardConfig();
	}

	@Override
	public CxDashboardConfigModel getDashboardConfigForCodeAndCurrentUser(final String code) {
		if (StringUtils.isBlank(code) || !(userService.getCurrentUser() instanceof CustomerModel customer)) {
			return null;
		}
		return dashboardDao.getDashboardConfigByCodeAndUser(code, customer);
	}

	@Override
	public List<CxDashboardConfigModel> getDashboardConfigsForCurrentUser() {
		if (!(userService.getCurrentUser() instanceof CustomerModel customer)) {
			return Collections.emptyList();
		}
		return dashboardDao.getAllDashboardConfigsByUser(customer);
	}

	@Override
	public CxDashboardConfigModel createDashboardConfigFromDefault() {
		final CxDashboardConfigModel copy = createNewDashboardConfig();
		if (copy == null) {
			return null;
		}

		final CxDashboardConfigModel defaultConfig = dashboardDao.getDefaultDashboardConfig();
		copy.setName(defaultConfig.getName());
		copy.setActive(true);

		final Collection<CxDashboardWidgetConfigModel> defaultWidgets = defaultConfig.getWidgetConfigs();
		if (defaultWidgets != null) {
			copy.setWidgetConfigs(defaultWidgets.stream()
					.map(defaultWidget -> copyWidgetConfig(defaultWidget, copy.getCode()))
					.toList());
		}

		modelService.save(copy);
		return copy;
	}

	protected CxDashboardWidgetConfigModel copyWidgetConfig(final CxDashboardWidgetConfigModel source, final String configCode) {
		final CxDashboardWidgetConfigModel widgetCopy = createNewDashboardWidgetConfig(source.getWidget().getCode(), configCode);
		widgetCopy.setPosition(source.getPosition());
		widgetCopy.setRowSpan(source.getRowSpan());
		widgetCopy.setColumnSpan(source.getColumnSpan());
		if (source.getSettings() != null) {
			widgetCopy.setSettings(source.getSettings().stream().map(setting -> {
				CxDashboardWidgetSettingModel copiedSetting = modelService.create(CxDashboardWidgetSettingModel.class);
				copiedSetting.setKey(setting.getKey());
				copiedSetting.setValue(setting.getValue());
				return copiedSetting;
			}).toList());
		}
		return widgetCopy;
	}

	@Override
	public CxDashboardConfigModel createNewDashboardConfig() {
		if (!(userService.getCurrentUser() instanceof CustomerModel customer)) {
			return null;
		}

		final List<CxDashboardConfigModel> existingConfigs = dashboardDao.getAllDashboardConfigsByUser(customer);
		final String uuid = UUID.randomUUID().toString();
		final CxDashboardConfigModel dashboardConfig = modelService.create(CxDashboardConfigModel.class);
		dashboardConfig.setCustomer(customer);
		dashboardConfig.setCode(String.format(CUSTOM_CONFIG_PATTERN, CUSTOM_CONFIG_PREFIX, customer.getUid(), uuid));
		dashboardConfig.setPosition(existingConfigs.size());
		return dashboardConfig;
	}

	@Override
	public String createCodeForWidgetConfig(CxDashboardWidgetConfigModel widgetConfig, final String configCode) {
		if (widgetConfig.getWidget().isMultipleOccurrenceAllowed()) {
			return String.format("%s_%s_%s", widgetConfig.getWidget().getContentComponent().getUid(), UUID.randomUUID().hashCode(), configCode);
		} else {
			return String.format("%s_%s", widgetConfig.getWidget().getContentComponent().getUid(), configCode);
		}
	}

	@Override
	public CxDashboardWidgetConfigModel createNewDashboardWidgetConfig(final String widgetCode, final String configCode) {
		final CxDashboardWidgetConfigModel cxDashboardWidgetConfigModel = modelService.create(CxDashboardWidgetConfigModel.class);
		CxDashboardWidgetModel widget = getDashboardWidgetByCode(widgetCode);
		cxDashboardWidgetConfigModel.setWidget(widget);
		cxDashboardWidgetConfigModel.setCode(createCodeForWidgetConfig(cxDashboardWidgetConfigModel, configCode));
		return cxDashboardWidgetConfigModel;
	}

	@Override
	public void removeWidgetConfigs(final CxDashboardConfigModel dashboardConfig) {
		final Collection<CxDashboardWidgetConfigModel> existingWidgetConfigs = dashboardConfig.getWidgetConfigs();
		dashboardConfig.setWidgetConfigs(null);
		if (existingWidgetConfigs != null) {
			existingWidgetConfigs.forEach(widgetConfig -> {
				widgetConfig.getSettings().forEach(modelService::remove);
				modelService.remove(widgetConfig);
			});
		}
		modelService.save(dashboardConfig);
	}

	@Override
	public void saveDashboardConfig(final CxDashboardConfigModel dashboardConfig) {
		modelService.save(dashboardConfig);
		final Collection<CxDashboardWidgetConfigModel> widgetConfigs = dashboardConfig.getWidgetConfigs();
		if (widgetConfigs != null && !widgetConfigs.isEmpty()) {
			modelService.saveAll(widgetConfigs);
		}
	}

	@Override
	public void deleteDashboardConfigForCurrentUser(final String configCode) {
		final CxDashboardConfigModel existingConfig = getDashboardConfigForCodeAndCurrentUser(configCode);
		if (existingConfig == null) {
			return;
		}
		List<CxDashboardConfigModel> dashboardConfigsForCurrentUser = getDashboardConfigsForCurrentUser();
		if (dashboardConfigsForCurrentUser.size() <= 1) {
			throw new IllegalArgumentException("The last dashboard configuration cannot be deleted");
		}
		updatePositionOnDeletion(dashboardConfigsForCurrentUser, existingConfig);
		removeWidgetConfigs(existingConfig);
		modelService.remove(existingConfig);
	}

	@Override
	public CxDashboardWidgetModel getDashboardWidgetByCode(String code) {
		return getAllAvailableWidgets().stream()
				.filter(widget -> widget.getCode().equals(code))
				.findFirst()
				.orElse(null);
	}

	@Override
	public Collection<CxDashboardWidgetModel> getAllAvailableWidgets() {
		BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null) {
			return baseSite.getAvailableDashboardWidgets();
		}
		return List.of();
	}

	protected void updatePositionOnDeletion(List<CxDashboardConfigModel> dashboardConfigs, CxDashboardConfigModel deletedConfig) {
		int deletedPosition = deletedConfig.getPosition();
		for (CxDashboardConfigModel config : dashboardConfigs) {
			if (config.getPosition() > deletedPosition) {
				config.setPosition(config.getPosition() - 1);
				modelService.save(config);
			}
		}
	}
}
