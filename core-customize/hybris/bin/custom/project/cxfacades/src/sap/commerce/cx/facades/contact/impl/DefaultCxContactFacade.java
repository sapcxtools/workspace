package sap.commerce.cx.facades.contact.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sap.commerce.cx.core.contact.CxContactService;
import sap.commerce.cx.facades.contact.CxContactFacade;
import sap.commerce.cx.facades.data.contact.ContactData;

public class DefaultCxContactFacade implements CxContactFacade {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCxContactFacade.class);

	private final CxContactService cxContactService;

	public DefaultCxContactFacade(final CxContactService cxContactService) {
		this.cxContactService = cxContactService;
	}

	@Override
	public void sendContact(final ContactData contactData) {
		cxContactService.sendContactMail(buildContextParameters(contactData));
	}

	private Map<String, Object> buildContextParameters(final ContactData contactData) {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("email", contactData.getEmail());
		parameters.put("firstName", contactData.getFirstName());
		parameters.put("lastName", contactData.getLastName());
		parameters.put("subject", contactData.getSubject());
		parameters.put("message", contactData.getMessage());

		return parameters;
	}
}
