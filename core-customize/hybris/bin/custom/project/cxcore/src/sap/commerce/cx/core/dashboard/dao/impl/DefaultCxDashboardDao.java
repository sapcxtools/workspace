package sap.commerce.cx.core.dashboard.dao.impl;

import static sap.commerce.cx.core.constants.CxCoreConstants.DASHBOARD.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import sap.commerce.cx.core.dashboard.dao.CxDashboardDao;
import sap.commerce.cx.core.model.CxDashboardConfigModel;

/**
 * Default DAO implementation for accessing dashboard configurations.
 */
public class DefaultCxDashboardDao extends DefaultGenericDao<CxDashboardConfigModel> implements CxDashboardDao {

	public DefaultCxDashboardDao() {
		super(CxDashboardConfigModel._TYPECODE);
	}

	@Override
	public CxDashboardConfigModel getDefaultDashboardConfig() {
		final Map<String, String> params = new HashMap<>();
		params.put(CxDashboardConfigModel.CODE, DEFAULT_CONFIG_CODE);

		final List<CxDashboardConfigModel> result = find(params);
		if (result.size() == 1) {
			return result.getFirst();
		} else if (result.isEmpty()) {
			throw new ModelNotFoundException("Could not find default dashboard config");
		} else {
			throw new AmbiguousIdentifierException("Could not find unique default dashboard config");
		}
	}

	@Override
	public CxDashboardConfigModel getDashboardConfigByCodeAndUser(final String code, final CustomerModel customer) {
		final Map<String, Object> params = new HashMap<>();
		params.put(CxDashboardConfigModel.CODE, code);
		params.put(CxDashboardConfigModel.CUSTOMER, customer);

		final List<CxDashboardConfigModel> result = find(params);
		if (result.size() == 1) {
			return result.getFirst();
		} else if (result.isEmpty()) {
			return null;
		} else {
			throw new AmbiguousIdentifierException(String.format("Could not find unique dashboard config for code '%s' and user '%s'", code, customer.getUid()));
		}
	}

	@Override
	public List<CxDashboardConfigModel> getAllDashboardConfigsByUser(final CustomerModel customer) {
		final Map<String, Object> params = new HashMap<>();
		params.put(CxDashboardConfigModel.CUSTOMER, customer);

		return find(params);
	}
}
