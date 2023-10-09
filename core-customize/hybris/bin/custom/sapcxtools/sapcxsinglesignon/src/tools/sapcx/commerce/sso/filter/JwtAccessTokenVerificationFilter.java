package tools.sapcx.commerce.sso.filter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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
public class JwtAccessTokenVerificationFilter extends OncePerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JwtAccessTokenVerificationFilter.class);

	private OAuth2RequestFactory oAuth2RequestFactory;
	private ClientDetailsService clientDetailsService;
	private UserDetailsService userDetailsService;
	private TokenStore tokenStore;
	private String occClientId;
	private boolean enabled;
	private String issuer;
	private String audience;
	private String customerIdField;
	private TokenExtractor tokenExtractor;
	private JwtDecoder jwtDecoder = null;

	public JwtAccessTokenVerificationFilter(
			OAuth2RequestFactory oAuth2RequestFactory,
			ClientDetailsService clientDetailsService,
			UserDetailsService userDetailsService,
			TokenStore tokenStore,
			String occClientId,
			boolean enabled,
			String issuer,
			String audience,
			String customerIdField) {
		this.oAuth2RequestFactory = oAuth2RequestFactory;
		this.clientDetailsService = clientDetailsService;
		this.userDetailsService = userDetailsService;
		this.tokenStore = tokenStore;
		this.occClientId = occClientId;
		this.enabled = enabled;
		this.issuer = issuer;
		this.audience = audience;
		this.customerIdField = customerIdField;
	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		this.tokenExtractor = new BearerTokenExtractor();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Authentication accessToken = tokenExtractor.extract(request);
		if (enabled && accessToken != null) {
			String accessTokenValue = accessToken.getPrincipal().toString();
			LOG.debug("Access token extracted from request: {}", accessTokenValue);

			OAuth2AccessToken oAuth2AccessToken = fetchFromTokenStore(accessTokenValue, false);
			LOG.debug("OAuth2 AccessToken from token store (1st attempt, without lock): {} (expired? => {})", oAuth2AccessToken,
					oAuth2AccessToken != null ? oAuth2AccessToken.isExpired() : false);

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
		} else if (oAuth2AccessToken != null && oAuth2AccessToken.isExpired()) {
			tokenStore.removeAccessToken(oAuth2AccessToken);
			oAuth2AccessToken = null;
		}

		if (createIfMissing) {
			try {
				Jwt decodedToken = decodeAccessToken(accessTokenValue);
				String userId = decodedToken.getClaimAsString(customerIdField);
				if (userId != null) {
					LOG.debug("Mapped user ID using field '{}': '{}'", customerIdField, userId);
					oAuth2AccessToken = storeAuthenticationForUser(userId, occClientId, decodedToken);
				} else {
					LOG.warn("No user ID found in access token for field: '{}'. Make sure your IDP configuration is correct!", customerIdField);
				}
			} catch (Exception e) {
				LOG.debug(String.format("Invalid access token '%s'!", accessTokenValue), e);
			}
		}

		return oAuth2AccessToken;
	}

	protected Jwt decodeAccessToken(String accessTokenValue) throws JwtException {
		if (jwtDecoder == null) {
			initJwtDecoder();
		}

		try {
			return jwtDecoder.decode(accessTokenValue);
		} catch (JwtException e) {
			LOG.debug("Retry with reinitialized decoder");
			initJwtDecoder();
			return jwtDecoder.decode(accessTokenValue);
		}
	}

	private OAuth2AccessToken storeAuthenticationForUser(String userId, String oAuth2ClientId, Jwt decodedToken) {
		assert isNotBlank(oAuth2ClientId);
		assert isNotBlank(userId);

		// OAuth2 Request
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(oAuth2ClientId);
		TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(Collections.emptyMap(), clientDetails);
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

		// Username Password Auth Token
		UsernamePasswordAuthenticationToken userToken = createUsernamePasswordAuthenticationToken(userId);

		// Create access token and authentication for user
		DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(decodedToken.getTokenValue());
		oAuth2AccessToken.setExpiration(Date.from(decodedToken.getExpiresAt()));
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

	private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(String userId) {
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
			return new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());
		} catch (UsernameNotFoundException e) {
			LOG.warn("Login attempt for unknown user '{}'!", userId);
			throw new BadCredentialsException("Invalid credentials!");
		}
	}

	protected void configureValidationForJwtDecoder(NimbusJwtDecoder decoder) {
		OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
		decoder.setJwtValidator(withAudience);
	}

	private synchronized void initJwtDecoder() {
		if (this.jwtDecoder == null) {
			this.jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);
			if (this.jwtDecoder instanceof NimbusJwtDecoder) {
				configureValidationForJwtDecoder((NimbusJwtDecoder) this.jwtDecoder);
			}
		}
	}
}
