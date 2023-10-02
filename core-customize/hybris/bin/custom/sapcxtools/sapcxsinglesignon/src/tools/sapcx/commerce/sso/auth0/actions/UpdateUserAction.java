package tools.sapcx.commerce.sso.auth0.actions;

import java.util.Map;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UpdateUserAction implements SdkAction<User> {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateUserAction.class);

	static User updateUser(User user, CustomerModel customer) throws Auth0Exception {
		return new UpdateUserAction().execute(Map.of("user", user, "customer", customer));
	}

	private UpdateUserAction() {
		// Avoid instantiation
	}

	@Override
	public User execute(Map<String, Object> parameter) throws Auth0Exception {
		User user = getWithType(parameter, "user", User.class);
		CustomerModel customer = getWithType(parameter, "customer", CustomerModel.class);
		String customerId = customer.getUid();
		try {
			Converter<CustomerModel, User> customerConverter = getCustomerConverter();
			User userInfo = customerConverter.convert(customer);
			return user = fetch(managementAPI().users().update(user.getId(), userInfo));
		} catch (Auth0Exception exception) {
			LOG.debug(String.format("Search for user with ID '%s' failed!", customer.getUid()), exception);
			throw exception;
		} finally {
			LOG.debug("Update information for existing user with ID '{}' resulted in: '{}'", customerId, user != null ? user.getId() : "-error-");
		}
	}
}
