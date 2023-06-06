package tools.sapcx.commerce.sso.auth0.actions;

import java.util.Map;

import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FetchUserAction implements SdkAction<User> {
	private static final Logger LOG = LoggerFactory.getLogger(FetchUserAction.class);

	static User getUser(String email) throws Auth0Exception {
		return new FetchUserAction().execute(Map.of("email", email));
	}

	private FetchUserAction() {
		// Avoid instantiation
	}

	@Override
	public User execute(Map<String, Object> parameter) throws Auth0Exception {
		String email = getWithType(parameter, "email", String.class);
		User user = null;
		try {
			UserFilter userByEmail = new UserFilter().withQuery(email).withFields("email", true);
			return user = fetchFirst(managementAPI().users().list(userByEmail));
		} catch (Auth0Exception exception) {
			LOG.debug(String.format("Search for user with email '%s' could not be executed!", email), exception);
			throw exception;
		} finally {
			LOG.debug("Lookup for existing user with email '{}' resulted in: '{}'", email, user != null ? user.getId() : "-not found-");
		}
	}
}
