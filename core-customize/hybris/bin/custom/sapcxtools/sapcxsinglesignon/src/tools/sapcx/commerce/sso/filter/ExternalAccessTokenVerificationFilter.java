package tools.sapcx.commerce.sso.filter;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This filter performs verification of an external access token.
 *
 * If enabled, it will extract the access token from the request, and asks a
 * concrete implementation to fetch the user details.
 *
 * If valid, the user details will be used to create a local OAuth2
 * Authentication for the user and stored in the token store.
 *
 * Later in the filter chain, the Spring security chain will verify the access
 * token against the token store and use this token to authenticate the user.
 */
public abstract class ExternalAccessTokenVerificationFilter extends OncePerRequestFilter {
	public static final String REVALIDATE_TOKEN_PARAMETER = "revalidate_token";
	private static final Logger LOG = LoggerFactory.getLogger(ExternalAccessTokenVerificationFilter.class);

	private OAuth2RequestFactory oAuth2RequestFactory;
	private ClientDetailsService clientDetailsService;
	private UserDetailsService userDetailsService;
	private TokenStore tokenStore;
	private String occClientId;
	private int tokenExpiration;
	private boolean enabled;
	private TokenExtractor tokenExtractor;

	public ExternalAccessTokenVerificationFilter(
			OAuth2RequestFactory oAuth2RequestFactory,
			ClientDetailsService clientDetailsService,
			UserDetailsService userDetailsService,
			TokenStore tokenStore,
			String occClientId,
			int tokenExpiration,
			boolean enabled) {
		this.oAuth2RequestFactory = oAuth2RequestFactory;
		this.clientDetailsService = clientDetailsService;
		this.userDetailsService = userDetailsService;
		this.tokenStore = tokenStore;
		this.occClientId = occClientId;
		this.tokenExpiration = tokenExpiration;
		this.enabled = enabled;
	}

	@Nonnull
	protected abstract String getUserIdFromAccessToken(String accessTokenValue);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Authentication accessToken = tokenExtractor.extract(request);
		if (enabled && accessToken != null) {
			String accessTokenValue = accessToken.getPrincipal().toString();
			LOG.debug("Access token extracted from request: {}", accessTokenValue);

			OAuth2AccessToken oAuth2AccessToken = fetchFromTokenStore(accessTokenValue, false);
			LOG.debug("OAuth2 AccessToken from token store (1st attempt, without lock): {} (expired? => {})", oAuth2AccessToken,
					oAuth2AccessToken != null ? oAuth2AccessToken.isExpired() : false);

			String revalidateToken = defaultIfBlank(request.getParameter(REVALIDATE_TOKEN_PARAMETER), "false");
			if (oAuth2AccessToken != null && revalidateToken.equals("true")) {
				synchronized (accessTokenValue.intern()) {
					LOG.debug("Request revalidation of access token with request parameter: {}", REVALIDATE_TOKEN_PARAMETER);
					tokenStore.removeAccessToken(oAuth2AccessToken);
					LOG.debug("Access token removed from store: {}", accessTokenValue);
					oAuth2AccessToken = null;
				}
			}

			if (oAuth2AccessToken == null || oAuth2AccessToken.isExpired()) {
				synchronized (accessTokenValue.intern()) {
					oAuth2AccessToken = fetchFromTokenStore(accessTokenValue, true);
					LOG.debug("OAuth2 AccessToken from token store (2nd attempt, with lock): {} (expired? => {})", oAuth2AccessToken,
							oAuth2AccessToken != null ? oAuth2AccessToken.isExpired() : false);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private OAuth2AccessToken fetchFromTokenStore(String accessTokenValue, boolean createIfMissing) {
		OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessTokenValue);
		if (oAuth2AccessToken != null && !oAuth2AccessToken.isExpired()) {
			return oAuth2AccessToken;
		}

		if (createIfMissing) {
			String userId = getUserIdFromAccessToken(accessTokenValue);
			if (userId != null) {
				oAuth2AccessToken = storeAuthenticationForUser(accessTokenValue, occClientId, userId);
			}
		}

		return oAuth2AccessToken;
	}

	private OAuth2AccessToken storeAuthenticationForUser(String accessTokenValue, String oAuth2ClientId, String userId) {
		assert isNotBlank(oAuth2ClientId);
		assert isNotBlank(userId);

		// OAuth2 Request
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(oAuth2ClientId);
		TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(Collections.emptyMap(), clientDetails);
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

		// Username Password Auth Token
		UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
		UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());

		// Create access token and authentication for user
		DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessTokenValue);
		oAuth2AccessToken.setExpiration(calculateExpirationDate());
		OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userToken);

		// Remove existing access token for same authentication
		OAuth2AccessToken existingToken = tokenStore.getAccessToken(authentication);
		if (existingToken != null) {
			LOG.debug("Found another access token '{}' for the authentication, removing it from the token store.", existingToken.getValue());
			tokenStore.removeAccessToken(existingToken);
		}

		// Create the new access token for the user
		tokenStore.storeAccessToken(oAuth2AccessToken, authentication);
		LOG.debug("New access token has been created successfully!");

		return oAuth2AccessToken;
	}

	private Date calculateExpirationDate() {
		if (tokenExpiration <= 0) {
			return null;
		}
		return Date.from(Instant.now().plus(tokenExpiration, ChronoUnit.MINUTES));
	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		this.tokenExtractor = new BearerTokenExtractor();
	}
}
