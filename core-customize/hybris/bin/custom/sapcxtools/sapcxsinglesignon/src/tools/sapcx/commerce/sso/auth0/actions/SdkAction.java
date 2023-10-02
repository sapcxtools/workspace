package tools.sapcx.commerce.sso.auth0.actions;

import java.util.Map;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Page;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@FunctionalInterface
interface SdkAction<RESPONSE> {
	RESPONSE execute(Map<String, Object> requestParameter) throws Auth0Exception;

	default ManagementAPI managementAPI() throws Auth0Exception {
		return getConfigurationService().getManagementAPI();
	}

	default void submit(Request<?> request) throws Auth0Exception {
		request.execute();
	}

	default <T> T fetch(Request<T> request) throws Auth0Exception {
		return request.execute().getBody();
	}

	default <T> T fetchFirst(Request<? extends Page<T>> request) throws Auth0Exception {
		return request.execute().getBody().getItems().stream().findFirst().orElse(null);
	}

	default <T> T getWithType(Map<String, Object> requestParameter, String parameterName, Class<T> returnType) {
		Object value = requestParameter.get(parameterName);
		return (returnType.isInstance(value)) ? returnType.cast(value) : null;
	}

	default String getCustomerIdField() {
		return getConfigurationService().getCustomerIdField();
	}

	default String getCustomerConnection() {
		return getConfigurationService().getCustomerConnection();
	}

	default Converter<CustomerModel, User> getCustomerConverter() {
		return getConfigurationService().getCustomerConverter();
	}

	private SdkConfigurationService getConfigurationService() {
		return Registry.getApplicationContext().getBean(SdkConfigurationService.class);
	}
}
