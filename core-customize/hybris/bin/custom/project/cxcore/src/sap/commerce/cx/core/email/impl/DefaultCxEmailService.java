package sap.commerce.cx.core.email.impl;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.mail2.jakarta.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.servicelayer.i18n.I18NService;

import me.cxdev.commerce.toolkit.email.HtmlEmailService;
import me.cxdev.commerce.toolkit.email.ThymeleafHtmlEmailGenerator;
import sap.commerce.cx.core.email.CxEmailService;

/**
 * Default implementation of the email service.
 */
public class DefaultCxEmailService implements CxEmailService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCxEmailService.class);

	private final I18NService i18NService;
	private final HtmlEmailService htmlEmailService;
	private final ThymeleafHtmlEmailGenerator htmlEmailGenerator;

	public DefaultCxEmailService(final I18NService i18NService,
			final HtmlEmailService htmlEmailService,
			final ThymeleafHtmlEmailGenerator htmlEmailGenerator) {
		this.i18NService = i18NService;
		this.htmlEmailService = htmlEmailService;
		this.htmlEmailGenerator = htmlEmailGenerator;
	}

	@Override
	public HtmlEmail prepareMail(final String template,
			final String subject,
			final Map<String, Object> params,
			final List<String> recipientsTo,
			final List<String> recipientsCc) {

		if (CollectionUtils.isEmpty(recipientsTo)) {
			LOG.error("No valid recipient addresses found for email.");
			throw new IllegalArgumentException("No valid recipient addresses found for email.");
		}

		final Locale locale = i18NService.getCurrentLocale();
		try {
			return htmlEmailGenerator.newHtmlEmail()
					.subject(subject)
					.template(template, locale)
					.templateParameter(params)
					.custom(builder -> emptyIfNull(recipientsTo).forEach(builder::to))
					.custom(builder -> emptyIfNull(recipientsCc).forEach(builder::cc))
					.build();
		} catch (final Exception e) {
			LOG.error("Technical error creating email", e);
			throw new IllegalStateException("Technical error creating email", e);
		}
	}

	@Override
	public void sendMail(final HtmlEmail email) {
		try {
			htmlEmailService.sendEmail(email);
		} catch (final IllegalArgumentException e) {
			LOG.warn("Validation error sending email: {}", e.getMessage());

		} catch (final Exception e) {
			LOG.error("Technical error sending email", e);
		}
	}
}
