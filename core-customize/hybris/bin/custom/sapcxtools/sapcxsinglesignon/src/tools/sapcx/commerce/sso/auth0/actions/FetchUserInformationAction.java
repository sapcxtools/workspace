package tools.sapcx.commerce.sso.auth0.actions;

import java.util.Map;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FetchUserInformationAction implements SdkAction<UserInfo> {
	private static final Logger LOG = LoggerFactory.getLogger(FetchUserInformationAction.class);

	static UserInfo getUserInformation(String accessToken) throws Auth0Exception {
		return new FetchUserInformationAction().execute(Map.of("accessToken", accessToken));
	}

	private FetchUserInformationAction() {
		// Avoid instantiation
	}

	@Override
	public UserInfo execute(Map<String, Object> parameter) throws Auth0Exception {
		String accessToken = getWithType(parameter, "accessToken", String.class);
		UserInfo userInfo = null;
		try {
			userInfo = fetch(authAPI().userInfo(accessToken));
			return userInfo;
		} catch (Auth0Exception exception) {
			LOG.debug(String.format("Retrieval of user information for access token '%s' failed!", accessToken), exception);
			throw exception;
		} finally {
			LOG.debug("Retrieve information for access token '{}' resulted in: '{}'", accessToken, userInfo != null ? userInfo.getValues() : "-not found-");
		}
	}
}
