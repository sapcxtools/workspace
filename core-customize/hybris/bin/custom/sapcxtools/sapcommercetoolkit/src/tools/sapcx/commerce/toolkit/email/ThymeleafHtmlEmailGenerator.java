package tools.sapcx.commerce.toolkit.email;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;

import de.hybris.platform.util.mail.MailUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.assertj.core.util.VisibleForTesting;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * This {@link HtmlEmailGenerator} uses Thymeleaf as template engine and supports multiple template formats:
 *
 * <ul>
 *     <li>HTML: provided as html file in classpath:/email-templates/html/[name].html</li>
 *     <li>TEXT: provided as text file in classpath:/email-templates/text/[name].txt</li>
 *     <li>String: provided as plain {@link String} to the template engine itself</li>
 * </ul>
 * <p>
 * The templates can be placed in any extension, they just have to follow the naming conventions.
 */
public class ThymeleafHtmlEmailGenerator implements HtmlEmailGenerator {
	private ITemplateEngine templateEngine;

	public ThymeleafHtmlEmailGenerator(ITemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public HtmlEmail createHtmlEmail() throws EmailException {
		// MailUtils always returns an HtmlEmail
		HtmlEmail email = getPreConfiguredEmail();

		// Use UTF-8 as default charset
		email.setCharset("UTF-8");

		return email;
	}

	@VisibleForTesting
	protected HtmlEmail getPreConfiguredEmail() throws EmailException {
		return (HtmlEmail) MailUtils.getPreConfiguredEmail();
	}

	@Override
	public HtmlEmail createHtmlEmail(
			String subject,
			String body,
			Collection<InternetAddress> to,
			Collection<InternetAddress> cc,
			Collection<InternetAddress> bcc) throws EmailException {
		if (CollectionUtils.isEmpty(to)) {
			throw new EmailException(String.format("Cannot create email without recipients. Please provide at least one valid email address!"));
		}

		HtmlEmail email = createHtmlEmail();
		email.setTo(to);
		if (CollectionUtils.isNotEmpty(cc)) {
			email.setCc(cc);
		}
		if (CollectionUtils.isNotEmpty(bcc)) {
			email.setBcc(bcc);
		}
		email.setSubject(subject);
		email.setHtmlMsg(body);

		return email;
	}

	@Override
	public HtmlEmail createHtmlEmailFromTemplate(
			String subject,
			Collection<InternetAddress> to,
			Collection<InternetAddress> cc,
			Collection<InternetAddress> bcc,
			String template,
			Map<String, Object> contextParameters,
			Locale locale) throws EmailException {
		Context ctx = new Context(locale);
		ctx.setVariables(contextParameters);
		ctx.setVariable("subject", subject);
		ctx.setVariable("recipients", to.stream().map(InternetAddress::getAddress).collect(Collectors.toList()));
		String body = templateEngine.process(template, ctx);

		return createHtmlEmail(subject, body, to, cc, bcc);
	}
}
