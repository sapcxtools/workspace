package sap.commerce.cx.core.contact.impl;

import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.EMAIL_CONTACT_TEMPLATE;
import static sap.commerce.cx.core.constants.CxCoreConstants.EMAIL.EMAIL_REPLY_TO;

import java.util.List;
import java.util.Map;

import org.apache.commons.mail2.jakarta.HtmlEmail;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import sap.commerce.cx.core.contact.CxContactService;
import sap.commerce.cx.core.email.CxEmailService;

/**
 * Default implementation of the contact service.
 */
public class DefaultCxContactService implements CxContactService {

	private final ConfigurationService configurationService;
	private final CxEmailService cxEmailService;

	public DefaultCxContactService(final ConfigurationService configurationService,
			final CxEmailService cxEmailService) {
		this.configurationService = configurationService;
		this.cxEmailService = cxEmailService;
	}

	@Override
	public void sendContactMail(final Map<String, Object> params) {
		final String template = configurationService.getConfiguration().getString(EMAIL_CONTACT_TEMPLATE);
		final String subject = String.valueOf(params.get("subject"));
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
