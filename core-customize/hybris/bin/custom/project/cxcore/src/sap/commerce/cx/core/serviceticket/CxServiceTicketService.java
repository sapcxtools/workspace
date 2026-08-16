package sap.commerce.cx.core.serviceticket;

import java.util.List;
import java.util.Map;

import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import sap.commerce.cx.core.model.CxServiceTicketModel;
import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;

/**
 * Business service for creating and retrieving service tickets.
 */
public interface CxServiceTicketService {

	/**
	 * Returns all existing tickets.
	 *
	 * @return all service tickets
	 */
	List<CxServiceTicketModel> getAllTickets();

	/**
	 * Returns all tickets that reference the given machine code.
	 *
	 * @param machineCode the machine product code
	 * @return matching tickets
	 */
	List<CxServiceTicketModel> getAllTicketsWithMachineCode(String machineCode);

	/**
	 * Returns a single ticket by code.
	 *
	 * @param code the ticket code
	 * @return the matching ticket or {@code null}
	 */
	CxServiceTicketModel getTicketForCode(String code);

	/**
	 * Creates and persists a new service ticket from form data.
	 *
	 * @param formData the submitted service request form data
	 * @return the created ticket model
	 * @throws ModelSavingException   if ticket persistence fails
	 * @throws ServiceTicketException if ticket creation logic fails
	 */
	CxServiceTicketModel createServiceTicket(CxServiceTicketFormData formData)
			throws ModelSavingException, ServiceTicketException;

	/**
	 * Creates and sends a service ticket notification email.
	 * <p>
	 * The email subject is generated from a localized message pattern based on the
	 * ticket type and ticket code. The email body is rendered using the configured
	 * Thymeleaf template and the provided context parameters.
	 * </p>
	 *
	 * @param contextParameters Thymeleaf context variables used to render the email template
	 * @param ticket            service ticket containing the ticket details and recipient information
	 */
	void sendServiceTicketEmail(Map<String, Object> contextParameters, CxServiceTicketData ticket);
}
