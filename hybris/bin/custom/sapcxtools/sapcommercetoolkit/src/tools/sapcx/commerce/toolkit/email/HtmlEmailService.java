package tools.sapcx.commerce.toolkit.email;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * The {@link HtmlEmailService} interface introduces a simplified way of sending HTML emails from processes and
 * workflows, without the need of having a CMS template and page defined for the email to be sent, as required with
 * the SAP standard. It should be used in favor of the standard services.
 *
 * If one is using the acceleratorservices extension, they should override the DefaultEmailService by a delegation
 * service that generates the email and calls this service.
 */
public interface HtmlEmailService {
	String sendEmail(HtmlEmail email) throws EmailException;

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
	 * @param <T> the class of the html email object
	 * @return a proxy that guarantees, that the send method invokes the {@link HtmlEmailService#sendEmail(HtmlEmail)} method
	 */
	default <T extends HtmlEmail> T proxy(T email) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(email.getClass());
		enhancer.setCallback(
				(MethodInterceptor) (object, method, objects, methodProxy) -> {
					if ("send".equals(method.getName())) {
						return sendEmail(email);
					} else {
						return method.invoke(email, objects);
					}
				});
		return (T) enhancer.create();
	}
}
