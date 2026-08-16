package sap.commerce.cx.core.serviceticket;

import java.util.List;

import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;

/**
 * Service contract for sending service ticket emails.
 */
public interface CxTicketEmailService {

	/**
	 * Sends the service ticket request email using ticket data and submitted form fields.
	 *
	 * @param ticketData      the ticket payload for email rendering
	 * @param formFieldValues the submitted form field values
	 * @throws ServiceTicketException if email generation or sending fails
	 */
	void sendServiceTicketEmailRequest(CxServiceTicketData ticketData,
			List<CxServiceTicketFormFieldData> formFieldValues) throws ServiceTicketException;
}
