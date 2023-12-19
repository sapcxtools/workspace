package tools.sapcx.commerce.sso.saml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class IdpInitiatedSamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final Logger LOG = LoggerFactory.getLogger(IdpInitiatedSamlAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		if (authentication instanceof Saml2Authentication) {
			String relayStateURL = request.getParameter("RelayState");
			if (StringUtils.isNotBlank(relayStateURL)) {
				LOG.debug("Redirecting to RelayState Url: " + relayStateURL);
				getRedirectStrategy().sendRedirect(request, response, relayStateURL);
				return;
			}
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
