package sap.commerce.cx.facades.serviceticket;

import java.util.List;

import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;
import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.CxServiceTicketCountData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormFieldData;

/**
 * Facade for reading, searching and creating service tickets.
 *
 * @param <STATE>  search state type used by the content search layer
 * @param <RESULT> search result entry type returned by the content search layer
 */
public interface CxServiceTicketFacade<STATE, RESULT> {

	/**
	 * Returns all tickets that reference the given machine code.
	 *
	 * @param machineCode machine code to filter by
	 * @return list of matching service tickets, or an empty list
	 */
	List<CxServiceTicketData> getAllTicketsWithMachineCode(String machineCode);

	/**
	 * Returns a ticket for the given code.
	 *
	 * @param ticketCode code of the service ticket
	 * @return matching {@link CxServiceTicketData}
	 */

	CxServiceTicketData getTicketForCode(String ticketCode);

	/**
	 * Executes a paged ticket search.
	 *
	 * @param filters     Filter conditions that combine attribute keys and attribute values.
	 * @param query       encoded search term
	 * @param currentPage the current page number
	 * @param pageSize    the number of items per page
	 * @param sort        the sorting criteria
	 * @return paged search result
	 */
	ContentSearchPageData<STATE, RESULT> search(final String filters, String query, int currentPage, int pageSize, String sort);

	/**
	 * Creates a ticket from submitted form data.
	 *
	 * @param formData submitted service request form
	 * @return created ticket data
	 * @throws ServiceTicketException if ticket creation fails
	 */
	CxServiceTicketData createTicket(CxServiceTicketFormData formData) throws ServiceTicketException;

	/**
	 * Sends a notification email for the specified service ticket.
	 *
	 * @param ticket          the created service ticket containing ticket details and recipients
	 * @param formFieldValues the submitted form field values to include in the email content
	 * @throws ServiceTicketException if the email could not be generated or sent
	 */
	void sendServiceTicketEmail(CxServiceTicketData ticket,
			List<CxServiceTicketFormFieldData> formFieldValues) throws ServiceTicketException;

	/**
	 * Calculates the number of open and closed service requests.
	 * <p>
	 *
	 * @return a {@link CxServiceTicketCountData} containing the total number of
	 * open and closed service requests
	 */
	CxServiceTicketCountData getTicketsCounts(String currentCustomer);

}
