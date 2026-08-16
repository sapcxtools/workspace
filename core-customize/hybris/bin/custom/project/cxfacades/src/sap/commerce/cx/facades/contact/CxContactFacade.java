package sap.commerce.cx.facades.contact;

import sap.commerce.cx.facades.data.contact.ContactData;

public interface CxContactFacade {

	/**
	 * Sends a contact message.
	 *
	 * @param contactData the contact data to process
	 */
	void sendContact(ContactData contactData);
}
