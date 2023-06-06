package tools.sapcx.commerce.sso.auth0.filter;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

import tools.sapcx.commerce.sso.auth0.actions.Actions;
import tools.sapcx.commerce.sso.filter.ExternalAccessTokenVerificationFilter;

/**
 * This filter performs verification of an Auth0 access token against the Auth0 API.
 */
public class Auth0AccessTokenVerificationFilter extends ExternalAccessTokenVerificationFilter {
	private static final Logger LOG = LoggerFactory.getLogger(Auth0AccessTokenVerificationFilter.class);

	private String userIdField;

	public Auth0AccessTokenVerificationFilter(
			OAuth2RequestFactory oAuth2RequestFactory,
			ClientDetailsService clientDetailsService,
			UserDetailsService userDetailsService,
			TokenStore tokenStore,
			String occClientId,
			int tokenExpiration,
			boolean enabled,
			String userIdField) {
		super(oAuth2RequestFactory, clientDetailsService, userDetailsService, tokenStore, occClientId, tokenExpiration, enabled);
		this.userIdField = userIdField;
	}

	@Override
	protected String getUserIdFromAccessToken(String accessTokenValue) {
		try {
			UserInfo userDetails = Actions.getUserInformation(accessTokenValue);
			if (userDetails != null) {
				String userId = (String) emptyIfNull(userDetails.getValues()).getOrDefault(userIdField, null);
				LOG.debug("Mapped user ID using field '{}': '{}'", userIdField, userId != null ? userId : "-none-");
				return userId;
			}
		} catch (Auth0Exception e) {
			LOG.debug(String.format("No valid user found for access token '%s'!", accessTokenValue), e);
		}

		return null;
	}
}
