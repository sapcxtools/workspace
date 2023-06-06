package tools.sapcx.commerce.toolkit.email;

import org.apache.commons.mail.HtmlEmail;

public class HtmlEmailWithServiceInvocation extends HtmlEmail {
	private HtmlEmail delegate;
	private Object sendEmailHandler;

	public HtmlEmailWithServiceInvocation(HtmlEmail htmlEmail, Object sendEmailHandler) {
		this.delegate = htmlEmail;
		this.sendEmailHandler = sendEmailHandler;
	}

}
