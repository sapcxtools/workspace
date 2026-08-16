package sap.commerce.cx.core.dashboard.dao;

import java.util.List;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import sap.commerce.cx.core.model.CxDashboardConfigModel;

/**
 * DAO for accessing dashboard configuration data.
 * <p>
 * Provides access methods for retrieving the default dashboard configuration
 * as well as user-specific dashboard configurations.
 * </p>
 */
public interface CxDashboardDao extends GenericDao<CxDashboardConfigModel> {

	/**
	 * Returns the default dashboard configuration.
	 *
	 * @return the default dashboard configuration
	 */
	CxDashboardConfigModel getDefaultDashboardConfig();

	/**
	 * Returns a dashboard configuration identified by its code and the given user.
	 *
	 * @param code the dashboard configuration code
	 * @param customer the customer for whom the dashboard configuration should be resolved
	 * @return the matching dashboard configuration
	 */
	CxDashboardConfigModel getDashboardConfigByCodeAndUser(final String code, final CustomerModel customer);

	/**
	 * Returns all dashboard configurations assigned to the given user.
	 *
	 * @param customer the customer whose dashboard configurations should be retrieved
	 * @return a list of dashboard configurations for the given user
	 */
	List<CxDashboardConfigModel> getAllDashboardConfigsByUser(final CustomerModel customer);
}
