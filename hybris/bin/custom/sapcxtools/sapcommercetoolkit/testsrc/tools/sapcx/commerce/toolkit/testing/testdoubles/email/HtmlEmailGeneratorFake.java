package tools.sapcx.commerce.toolkit.testing.testdoubles.email;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import tools.sapcx.commerce.toolkit.email.HtmlEmailGenerator;

public class HtmlEmailGeneratorFake implements HtmlEmailGenerator {
	@Override
	public HtmlEmail createHtmlEmail() throws EmailException {
		return new HtmlEmail();
	}

	@Override
	public HtmlEmail createHtmlEmail(String subject, String body, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc)
			throws EmailException {
		HtmlEmail htmlEmail = createHtmlEmail();
		htmlEmail.setHostName("localhost");
		htmlEmail.setCharset("UTF-8");
		htmlEmail.setSubject(subject);
		htmlEmail.setHtmlMsg(body);
		htmlEmail.setTo(to);
		htmlEmail.setCc(cc);
		htmlEmail.setBcc(bcc);
		return htmlEmail;
	}

	@Override
	public HtmlEmail createHtmlEmailFromTemplate(String subject, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc, String template,
			Map<String, Object> contextParameters, Locale locale) throws EmailException {
		StringBuilder builder = new StringBuilder();
		builder.append("Template: ").append(template).append("\n\n");
		builder.append("Locale: ").append(locale).append("\n\n");
		builder.append(contextParameters);
		return createHtmlEmail(subject, builder.toString(), to, cc, bcc);
	}
}
