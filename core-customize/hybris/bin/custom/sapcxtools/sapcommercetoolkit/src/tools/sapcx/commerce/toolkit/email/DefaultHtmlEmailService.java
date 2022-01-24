package tools.sapcx.commerce.toolkit.email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class DefaultHtmlEmailService implements HtmlEmailService {
	@Override
	public String sendEmail(HtmlEmail email) throws EmailException {
		return email.send();
	}
}
