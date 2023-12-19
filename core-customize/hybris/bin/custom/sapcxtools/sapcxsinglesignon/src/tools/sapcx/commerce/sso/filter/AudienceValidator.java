package tools.sapcx.commerce.sso.filter;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidator implements OAuth2TokenValidator<Jwt> {
	private String audience;

	public AudienceValidator(String audience) {
		this.audience = audience;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		if (token.getAudience().contains(audience)) {
			return OAuth2TokenValidatorResult.success();
		}

		OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);
		return OAuth2TokenValidatorResult.failure(error);
	}
}
