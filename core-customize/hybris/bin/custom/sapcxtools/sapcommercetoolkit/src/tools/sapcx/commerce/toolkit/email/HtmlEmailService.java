package tools.sapcx.commerce.toolkit.email;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * The {@link HtmlEmailService} interface introduces a simplified way of sending HTML emails from processes and
 * workflows, without the need of having a CMS template and page defined for the email to be sent, as required with
 * the SAP standard. It should be used in favor of the standard services.
 * <p>
 * If one is using the acceleratorservices extension, they should override the DefaultEmailService by a delegation
 * service that generates the email and calls this service.
 */
public interface HtmlEmailService {
	/**
	 * Sends the email. Internally we unwrap the proxy created by the {@link #proxy(HtmlEmail)} method and
	 * calls the {@link #sendEmailInternal(HtmlEmail)} method with its result.
	 *
	 * Note: This method must not be overridden! Please implement {@link #sendEmailInternal(HtmlEmail)} instead.
	 *
	 * @param email the email to be sent
	 * @return the message id of the underlying MimeMessage
	 * @throws EmailException the sending failed
	 */
	default String sendEmail(HtmlEmail email) throws EmailException {
		if (email instanceof ProxyHtmlEmail proxyHtmlEmail) {
			return sendEmailInternal(proxyHtmlEmail.getProxiedHtmlEmail());
		} else {
			return sendEmailInternal(email);
		}
	}

	/**
	 * Sends the email.
	 *
	 * @param email the email to be sent
	 * @return the message id of the underlying MimeMessage
	 * @throws EmailException the sending failed
	 */
	String sendEmailInternal(HtmlEmail email) throws EmailException;

	default InternetAddress getInternetAddress(String emailAddress) throws EmailException {
		return getInternetAddress(emailAddress, null);
	}

	default InternetAddress getInternetAddress(String emailAddress, String displayName) throws EmailException {
		try {
			InternetAddress address = new InternetAddress(emailAddress);
			address.setPersonal(StringUtils.isNotBlank(displayName) ? displayName : emailAddress);
			address.validate();
			return address;
		} catch (AddressException | UnsupportedEncodingException e) {
			throw new EmailException(e);
		}
	}

	/**
	 * This method shall only be used to enhance processes where SAP standard cannot be overwritten. Do not use it for emails
	 * that will be sent with the {@link HtmlEmailService} directly.
	 *
	 * @param email the email to be sent
	 * @return a proxy that guarantees, that the send method invokes current {@link HtmlEmailService#sendEmail(HtmlEmail)} method
	 */
	default HtmlEmail proxy(HtmlEmail email) throws IllegalArgumentException {
		return new ProxyHtmlEmail(this, email);
	}
}
