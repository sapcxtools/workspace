package tools.sapcx.commerce.toolkit.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;

public class HtmlEmailGeneratorTests {
	private String subjectFromParameter;
	private String bodyFromParameter;
	private Collection<InternetAddress> toFromParameter;
	private Collection<InternetAddress> ccFromParameter;
	private Collection<InternetAddress> bccFromParameter;
	private String templateFromParameter;
	private Map<String, Object> contextParametersFromParameter;
	private Locale localeFromParameter;

	private HtmlEmailGenerator htmlEmailGenerator;

	@Before
	public void setUp() throws Exception {
		htmlEmailGenerator = new HtmlEmailGenerator() {
			@Override
			public HtmlEmail createHtmlEmail() throws EmailException {
				return new HtmlEmail();
			}

			@Override
			public HtmlEmail createHtmlEmail(String subject, String body, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc)
					throws EmailException {
				subjectFromParameter = subject;
				bodyFromParameter = body;
				toFromParameter = to;
				ccFromParameter = cc;
				bccFromParameter = bcc;

				return createHtmlEmail();
			}

			@Override
			public HtmlEmail createHtmlEmailFromTemplate(String subject, Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc,
					String template, Map<String, Object> contextParameters, Locale locale) throws EmailException {
				subjectFromParameter = subject;
				toFromParameter = to;
				ccFromParameter = cc;
				bccFromParameter = bcc;
				templateFromParameter = template;
				contextParametersFromParameter = contextParameters;
				localeFromParameter = locale;
				return createHtmlEmail();
			}
		};
	}

	@Test
	public void defaultImplementationWithSubjectBodyAndToDelegatesToOverloadedMethod() throws Exception {
		htmlEmailGenerator.createHtmlEmail("subject", "body", Arrays.asList(InternetAddress.parse("to@local-dev")));

		assertThat(subjectFromParameter).isEqualTo("subject");
		assertThat(bodyFromParameter).isEqualTo("body");
		assertThat(toFromParameter).extracting("address").containsExactly("to@local-dev");
		assertThat(ccFromParameter).isNullOrEmpty();
		assertThat(bccFromParameter).isNullOrEmpty();
		assertThat(templateFromParameter).isNull();
		assertThat(contextParametersFromParameter).isNullOrEmpty();
		assertThat(localeFromParameter).isNull();
	}

	@Test
	public void defaultImplementationWithSubjectBodyAndToAndCcDelegatesToOverloadedMethod() throws Exception {
		List<InternetAddress> to = Arrays.asList(InternetAddress.parse("to@local-dev"));
		List<InternetAddress> cc = Arrays.asList(InternetAddress.parse("cc@local-dev"));
		htmlEmailGenerator.createHtmlEmail("subject", "body", to, cc);

		assertThat(subjectFromParameter).isEqualTo("subject");
		assertThat(bodyFromParameter).isEqualTo("body");
		assertThat(toFromParameter).extracting("address").containsExactly("to@local-dev");
		assertThat(ccFromParameter).extracting("address").containsExactly("cc@local-dev");
		assertThat(bccFromParameter).isNullOrEmpty();
		assertThat(templateFromParameter).isNull();
		assertThat(contextParametersFromParameter).isNullOrEmpty();
		assertThat(localeFromParameter).isNull();
	}

	@Test
	public void defaultImplementationWithSubjectTemplateAndToDelegatesToOverloadedMethod() throws Exception {
		List<InternetAddress> to = Arrays.asList(InternetAddress.parse("to@local-dev"));
		htmlEmailGenerator.createHtmlEmailFromTemplate("subject", to, "template", Map.of("param", "value"), Locale.GERMANY);

		assertThat(subjectFromParameter).isEqualTo("subject");
		assertThat(bodyFromParameter).isNull();
		assertThat(toFromParameter).extracting("address").containsExactly("to@local-dev");
		assertThat(ccFromParameter).isNullOrEmpty();
		assertThat(bccFromParameter).isNullOrEmpty();
		assertThat(templateFromParameter).isEqualTo("template");
		assertThat(contextParametersFromParameter).containsExactly(entry("param", "value"));
		assertThat(localeFromParameter).isEqualTo(Locale.GERMANY);
	}

	@Test
	public void defaultImplementationWithSubjectTemplateAndToAndCcDelegatesToOverloadedMethod() throws Exception {
		List<InternetAddress> to = Arrays.asList(InternetAddress.parse("to@local-dev"));
		List<InternetAddress> cc = Arrays.asList(InternetAddress.parse("cc@local-dev"));
		htmlEmailGenerator.createHtmlEmailFromTemplate("subject", to, cc, "template", Map.of("param", "value"), Locale.GERMANY);

		assertThat(subjectFromParameter).isEqualTo("subject");
		assertThat(bodyFromParameter).isNull();
		assertThat(toFromParameter).extracting("address").containsExactly("to@local-dev");
		assertThat(ccFromParameter).extracting("address").containsExactly("cc@local-dev");
		assertThat(bccFromParameter).isNullOrEmpty();
		assertThat(templateFromParameter).isEqualTo("template");
		assertThat(contextParametersFromParameter).containsExactly(entry("param", "value"));
		assertThat(localeFromParameter).isEqualTo(Locale.GERMANY);
	}

}
