package sap.commerce.cx.facades.dashboard;

import java.util.List;

import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;

/**
 * Facade for dashboard configuration operations exposed to the presentation layer.
 * <p>
 * Provides access to default and user-specific dashboard configurations as well as
 * persistence and deletion operations for the currently authenticated customer.
 * </p>
 */
public interface CxDashboardFacade {
	/**
	 * Returns the default dashboard configuration.
	 *
	 * @return the default dashboard configuration
	 */
	CxDashboardConfigData getDefaultDashboardConfig();

	/**
	 * Returns all dashboard configurations assigned to the currently authenticated customer.
	 *
	 * @return a list of dashboard configurations for the current customer
	 */
	List<CxDashboardConfigData> getDashboardConfigsForCurrentUser();

	/**
	 * Saves the given dashboard configuration for the currently authenticated customer.
	 *
	 * @param config the dashboard configuration to save
	 * @return the persisted dashboard configuration
	 */
	CxDashboardConfigData saveDashboardConfigForCurrentUser(CxDashboardConfigData config);

	/**
	 * Deletes the dashboard configuration identified by the given code
	 * for the currently authenticated customer.
	 *
	 * @param configCode the dashboard configuration code
	 */
	void deleteDashboardConfigForCurrentUser(String configCode);

	/**
	 * Returns all Dashboard Widgets maintained on the current BaseSite
	 *
	 * @return list of all available Dashboard Widgets
	 */
	List<CxDashboardWidgetData> getAllAvailableDashboardWidgets();
}
