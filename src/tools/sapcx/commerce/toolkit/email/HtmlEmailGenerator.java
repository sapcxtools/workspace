package tools.sapcx.commerce.toolkit.email;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * The {@link HtmlEmailGenerator} interface introduces a simplified way of generating HTML emails from processes and
 * workflows, without the need of having a CMS template and page defined for the email to be sent, as required with
 * the SAP standard.
 * <p>
 * This process shall not replace the way emails are generated within the system if the emails contain editorial
 * content, but rather for emails, that do not require editorial content and make it easier to generate them.
 */
public interface HtmlEmailGenerator {
	HtmlEmail createHtmlEmail() throws EmailException;

	HtmlEmail createHtmlEmail(String subject, String body, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc) throws EmailException;

	default HtmlEmail createHtmlEmail(String subject, String body, Collection<InternetAddress> to, Collection<InternetAddress> cc) throws EmailException {
		return createHtmlEmail(subject, body, to, cc, null);
	}

	default HtmlEmail createHtmlEmail(String subject, String body, Collection<InternetAddress> to) throws EmailException {
		return createHtmlEmail(subject, body, to, null, null);
	}

	HtmlEmail createHtmlEmailFromTemplate(String subject, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc, String template,
										  Map<String, Object> contextParameters, Locale locale) throws EmailException;

	default HtmlEmail createHtmlEmailFromTemplate(String subject, Collection<InternetAddress> to, Collection<InternetAddress> cc, String template,
			Map<String, Object> contextParameters, Locale locale) throws EmailException {
		return createHtmlEmailFromTemplate(subject, to, cc, null, template, contextParameters, locale);
	}

	default HtmlEmail createHtmlEmailFromTemplate(String subject, Collection<InternetAddress> to, String template,
												  Map<String, Object> contextParameters, Locale locale) throws EmailException {
		return createHtmlEmailFromTemplate(subject, to, null, null, template, contextParameters, locale);
	}
}
