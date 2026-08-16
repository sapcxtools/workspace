package sap.commerce.cx.core.dashboard.service;

import java.util.Collection;
import java.util.List;

import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;

/**
 * Service interface for managing dashboard configurations
 * of the currently authenticated customer.
 * <p>
 * Provides operations for retrieving, creating, updating,
 * and deleting dashboard configurations and dashboard widgets.
 * </p>
 */
public interface CxDashboardService {

	/**
	 * Returns the default dashboard configuration.
	 *
	 * @return the default dashboard configuration
	 */
	CxDashboardConfigModel getDefaultDashboardConfig();

	/**
	 * Returns the dashboard configuration identified by the given code
	 * for the currently authenticated customer.
	 *
	 * @param code the dashboard configuration code
	 * @return the matching dashboard configuration, or {@code null} if none exists
	 */
	CxDashboardConfigModel getDashboardConfigForCodeAndCurrentUser(final String code);

	/**
	 * Returns all dashboard configurations assigned to the currently authenticated customer.
	 *
	 * @return a list of dashboard configurations for the current customer
	 */
	List<CxDashboardConfigModel> getDashboardConfigsForCurrentUser();

	/**
	 * Creates and persists a personal copy of the default dashboard configuration
	 * (including its widgets) for the currently authenticated customer.
	 *
	 * @return the persisted copy, or {@code null} if the current user is not a customer
	 */
	CxDashboardConfigModel createDashboardConfigFromDefault();

	/**
	 * Creates a new dashboard configuration instance.
	 * <p>
	 * The returned model is not persisted automatically and must be saved explicitly.
	 * </p>
	 *
	 * @return a new dashboard configuration model instance
	 */
	CxDashboardConfigModel createNewDashboardConfig();

	String createCodeForWidgetConfig(final CxDashboardWidgetConfigModel widgetConfig, final String configCode);

	/**
	 * Creates a new dashboard widget configuration instance.
	 * <p>
	 * The returned model is not persisted automatically and must be saved explicitly.
	 * </p>
	 *
	 * @param widgetCode the code of the dashboard widget for which the configuration is created
	 * @return a new dashboard widget configuration model instance
	 */
	CxDashboardWidgetConfigModel createNewDashboardWidgetConfig(final String widgetCode, final String configCode);

	/**
	 * Removes all widget configurations from the given dashboard config and deletes their models.
	 *
	 * @param dashboardConfig the dashboard configuration whose widgets should be removed
	 */
	void removeWidgetConfigs(final CxDashboardConfigModel dashboardConfig);

	/**
	 * Saves the given dashboard configuration for the currently authenticated customer.
	 *
	 * @param config the dashboard configuration to save
	 */
	void saveDashboardConfig(final CxDashboardConfigModel config);

	/**
	 * Deletes the dashboard configuration identified by the given code
	 * for the currently authenticated customer.
	 * <p>
	 * The customer's last remaining dashboard configuration cannot be deleted.
	 * </p>
	 *
	 * @param configCode the dashboard configuration code
	 * @throws IllegalArgumentException if the configuration to delete is the customer's last one
	 */
	void deleteDashboardConfigForCurrentUser(String configCode);

	CxDashboardWidgetModel getDashboardWidgetByCode(final String code);

	Collection<CxDashboardWidgetModel> getAllAvailableWidgets();
}
