package tools.sapcx.commerce.toolkit.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

@UnitTest
public class ThymeleafHtmlEmailGeneratorTests {
	private HtmlEmail email;
	private ITemplateEngine templateEngine;
	private ThymeleafHtmlEmailGeneratorWithPreconfiguredEmail generator;

	@Before
	public void setUp() throws Exception {
		email = mock(HtmlEmail.class);
		when(email.getHostName()).thenReturn("localhost");
		when(email.getFromAddress()).thenReturn(InternetAddress.parse("from@local-dev")[0]);
		when(email.getReplyToAddresses()).thenReturn(Arrays.asList(InternetAddress.parse("reply-to@local.dev")));

		templateEngine = mock(ITemplateEngine.class);

		generator = new ThymeleafHtmlEmailGeneratorWithPreconfiguredEmail(templateEngine);
	}

	@Test
	public void verifyCharsetIsSetToUTF8() throws EmailException {
		generator.createHtmlEmail();
		verify(email).setCharset("UTF-8");
	}

	@Test(expected = EmailException.class)
	public void throwsExceptionIfToAddressIsEmpty() throws EmailException {
		generator.createHtmlEmail("subject", "body", List.of(), null, null);
	}

	@Test
	public void verifySetOfPropertiesWithoutCCandBCC() throws EmailException, AddressException {
		Collection<InternetAddress> to = Arrays.asList(InternetAddress.parse("to@local.dev"));
		generator.createHtmlEmail("subject", "body", to, null, null);
		verify(email).setCharset("UTF-8");
		verify(email).setSubject("subject");
		verify(email).setHtmlMsg("body");
		verify(email).setTo(to);
		verifyNoMoreInteractions(email);
	}

	@Test
	public void verifySetOfPropertiesWithCCandBCC() throws EmailException, AddressException {
		Collection<InternetAddress> to = Arrays.asList(InternetAddress.parse("to-1@local.dev, to-2@local.dev"));
		Collection<InternetAddress> cc = Arrays.asList(InternetAddress.parse("cc-1@local.dev, cc-2@local.dev, cc-3@local.dev"));
		Collection<InternetAddress> bcc = Arrays.asList(InternetAddress.parse("bcc@local.dev"));
		generator.createHtmlEmail("subject", "body", to, cc, bcc);
		verify(email).setCharset("UTF-8");
		verify(email).setSubject("subject");
		verify(email).setHtmlMsg("body");
		verify(email).setTo(to);
		verify(email).setCc(cc);
		verify(email).setBcc(bcc);
		verifyNoMoreInteractions(email);
	}

	@Test
	public void verifyTemplateEngineIsExecutedForTemplate() throws EmailException, AddressException {
		Collection<InternetAddress> to = Arrays.asList(InternetAddress.parse("to-1@local.dev, to-2@local.dev"));
		Collection<InternetAddress> cc = Arrays.asList(InternetAddress.parse("cc-1@local.dev, cc-2@local.dev, cc-3@local.dev"));
		Collection<InternetAddress> bcc = Arrays.asList(InternetAddress.parse("bcc@local.dev"));

		ArgumentCaptor<IContext> contextArgumentCaptor = ArgumentCaptor.forClass(IContext.class);
		when(templateEngine.process(eq("template"), contextArgumentCaptor.capture())).thenReturn("body-from-template");

		Map<String, Object> variables = Map.of("varA", "valueA", "varB", "valueB", "varC", "valueC");
		generator.createHtmlEmailFromTemplate("subject", to, cc, bcc, "template", variables, Locale.GERMANY);

		verify(email).setCharset("UTF-8");
		verify(email).setSubject("subject");
		verify(email).setHtmlMsg("body-from-template");
		verify(email).setTo(to);
		verify(email).setCc(cc);
		verify(email).setBcc(bcc);
		verifyNoMoreInteractions(email);

		IContext context = contextArgumentCaptor.getValue();
		assertThat(context.getLocale()).isEqualTo(Locale.GERMANY);
		assertThat(context.getVariable("subject")).isEqualTo("subject");
		assertThat(context.getVariable("recipients")).asList().containsExactly("to-1@local.dev", "to-2@local.dev");
		assertThat(context.getVariable("varA")).isEqualTo("valueA");
		assertThat(context.getVariable("varB")).isEqualTo("valueB");
		assertThat(context.getVariable("varC")).isEqualTo("valueC");
	}

	@Test
	public void verifyTemplateSubjectAndRecipientsMayNotBeOverriddenThroughVariables() throws EmailException, AddressException {
		Collection<InternetAddress> to = Arrays.asList(InternetAddress.parse("to-1@local.dev, to-2@local.dev"));

		ArgumentCaptor<IContext> contextArgumentCaptor = ArgumentCaptor.forClass(IContext.class);
		when(templateEngine.process(eq("template"), contextArgumentCaptor.capture())).thenReturn("body-from-template");

		Map<String, Object> variables = Map.of("subject", "new-subject", "recipients", "modified-recipients");
		generator.createHtmlEmailFromTemplate("subject", to, null, null, "template", variables, Locale.GERMANY);

		IContext context = contextArgumentCaptor.getValue();
		assertThat(context.getVariable("subject")).isEqualTo("subject");
		assertThat(context.getVariable("recipients")).asList().containsExactly("to-1@local.dev", "to-2@local.dev");
	}

	private class ThymeleafHtmlEmailGeneratorWithPreconfiguredEmail extends ThymeleafHtmlEmailGenerator {
		public ThymeleafHtmlEmailGeneratorWithPreconfiguredEmail(ITemplateEngine templateEngine) {
			super(templateEngine);
		}

		@Override
		protected HtmlEmail getPreConfiguredEmail() throws EmailException {
			return email;
		}
	}
}
