package sap.commerce.cx.core.registration.service.impl;

import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.*;
import static sap.commerce.cx.core.util.CxUtils.getLocalizedString;

import java.util.List;
import java.util.Map;

import org.apache.commons.mail2.jakarta.HtmlEmail;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import sap.commerce.cx.core.email.CxEmailService;
import sap.commerce.cx.core.registration.service.CxRegistrationService;

/**
 * Default implementation of {@link CxRegistrationService}.
 * <p>
 * This service creates and sends registration emails based on configured
 * subject and template properties and the provided template parameters.
 * </p>
 */
public class DefaultCxRegistrationService implements CxRegistrationService {

	private final ConfigurationService configurationService;
	private final CxEmailService cxEmailService;

	public DefaultCxRegistrationService(final ConfigurationService configurationService,
			final CxEmailService cxEmailService) {
		this.configurationService = configurationService;
		this.cxEmailService = cxEmailService;
	}

	/**
	 * Sends a registration email using the configured subject, template,
	 * sender and reply-to settings.
	 *
	 * @param params the template and mail parameters
	 */
	@Override
	public void sendRegistrationMail(final Map<String, Object> params) {
		final String template = configurationService.getConfiguration().getString(EMAIL_REGISTRATION_TEMPLATE);
		final String subject = getLocalizedString(EMAIL_REGISTRATION_SUBJECT, null);
		final String emailReplyTo = configurationService.getConfiguration().getString(EMAIL_REPLY_TO);

		final HtmlEmail email = cxEmailService.prepareMail(
				template,
				subject,
				params,
				List.of(emailReplyTo),
				List.of());
		cxEmailService.sendMail(email);
	}
}
