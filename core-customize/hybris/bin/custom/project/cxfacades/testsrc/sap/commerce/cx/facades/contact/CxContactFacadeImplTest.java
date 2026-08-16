package sap.commerce.cx.facades.contact;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.apache.commons.mail2.core.EmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;

import me.cxdev.commerce.toolkit.email.HtmlEmailService;
import sap.commerce.cx.facades.contact.impl.DefaultCxContactFacade;
import sap.commerce.cx.facades.data.contact.ContactData;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxContactFacadeImplTest {
	@Mock
	private HtmlEmailService emailService;

	@InjectMocks
	private DefaultCxContactFacade facade;

	@Test
	public void sendContact() throws EmailException {
		// setup
		final ContactData contact = new ContactData();
		final String email = "email@test.me";
		contact.setEmail(email);
		final String firstName = "Test";
		contact.setFirstName(firstName);
		final String lastName = "Customer";
		contact.setLastName(lastName);
		final String subject = "subject";
		contact.setSubject(subject);
		final String msg = "This is a test message";
		contact.setMessage(msg);

		// run
		facade.sendContact(contact);

		// verify
		verify(emailService, times(1)).sendEmail(any());
	}

	@Test
	public void sendContact_illegalArgumentException() throws EmailException {
		// setup
		final ContactData contact = new ContactData();
		final String email = "email@test.me";
		contact.setEmail(email);
		final String firstName = "Test";
		contact.setFirstName(firstName);
		final String lastName = "Customer";
		contact.setLastName(lastName);
		final String subject = "subject";
		contact.setSubject(subject);
		final String msg = "This is a test message";
		contact.setMessage(msg);
		doThrow(IllegalArgumentException.class).when(emailService).sendEmail(any());

		// run + verify
		assertThrows(RuntimeException.class, () -> facade.sendContact(contact));
	}

	@Test
	public void sendContact_emailException() throws EmailException {
		// setup
		final ContactData contact = new ContactData();
		final String email = "email@test.me";
		contact.setEmail(email);
		final String firstName = "Test";
		contact.setFirstName(firstName);
		final String lastName = "Customer";
		contact.setLastName(lastName);
		final String subject = "subject";
		contact.setSubject(subject);
		final String msg = "This is a test message";
		contact.setMessage(msg);
		doThrow(EmailException.class).when(emailService).sendEmail(any());

		// run + verify
		assertThrows(RuntimeException.class, () -> facade.sendContact(contact));
	}

	// TODO: test contactNull (currently would throw RuntimeException and log "Technical error sending contact" which is
	// not ideal logging behaviour
}
