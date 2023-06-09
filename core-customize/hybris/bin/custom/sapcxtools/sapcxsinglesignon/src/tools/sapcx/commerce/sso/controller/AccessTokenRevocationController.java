package tools.sapcx.commerce.sso.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/revokeAccessToken")
@Tag(name = "Users")
public class AccessTokenRevocationController {
	private static final Logger LOG = LoggerFactory.getLogger(AccessTokenRevocationController.class);

	@Resource(name = "oauthTokenStore")
	private TokenStore tokenStore;
	@Value("${sapcxsinglesignon.filter.enabled}")
	private boolean enabled;
	private TokenExtractor tokenExtractor;

	public AccessTokenRevocationController() {
		this.tokenExtractor = new BearerTokenExtractor();
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@Operation(operationId = "revokeAccessToken", summary = "Revokes an access token.")
	@ApiBaseSiteIdAndUserIdParam
	public void revokeAccessToken(HttpServletRequest request) {
		Authentication accessToken = tokenExtractor.extract(request);

		if (enabled && accessToken != null) {
			String accessTokenValue = accessToken.getPrincipal().toString();
			LOG.debug("Access token extracted from request: {}", accessTokenValue);

			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessTokenValue);
			LOG.debug("OAuth2 AccessToken found in token store: {}", oAuth2AccessToken != null ? "yes" : "no");

			if (oAuth2AccessToken != null) {
				synchronized (accessTokenValue.intern()) {
					tokenStore.removeAccessToken(oAuth2AccessToken);
					LOG.debug("Access token removed from store: {}", accessTokenValue);
				}
			}
		}
	}
}
