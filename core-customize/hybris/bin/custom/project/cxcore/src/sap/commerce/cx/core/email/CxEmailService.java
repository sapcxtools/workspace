package sap.commerce.cx.core.email;

import java.util.List;
import java.util.Map;

import org.apache.commons.mail2.jakarta.HtmlEmail;

public interface CxEmailService {

	/**
	 * Sends an HTML email.
	 *
	 * @param email the HTML email
	 */
	void sendMail(final HtmlEmail email);

	/**
	 * Creates an HTML email.
	 *
	 * @param template     the HTML template
	 * @param subject      the email subject
	 * @param params       the params to process
	 * @param recipientsTo the list of "to" recipients
	 * @param recipientsCc the list of "cc" recipients
	 * @return the HTML email
	 */
	HtmlEmail prepareMail(String template, String subject, Map<String, Object> params, List<String> recipientsTo, List<String> recipientsCc);
}
