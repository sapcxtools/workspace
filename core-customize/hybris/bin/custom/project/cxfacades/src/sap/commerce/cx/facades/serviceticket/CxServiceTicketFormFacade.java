package sap.commerce.cx.facades.serviceticket;

import java.util.List;

import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;

/**
 * This facade summarizes functions around the service request forms.
 */
public interface CxServiceTicketFormFacade {
	/**
	 * Returns a list of all service request forms.
	 *
	 * @return list data of all service request forms
	 */
	List<CxServiceTicketFormData> getAllServiceRequestForms();

	/**
	 * Find service request form for given id.
	 *
	 * @param id id of the service request form
	 * @return service request form data
	 */
	CxServiceTicketFormData getServiceRequestFormForId(String id);

}
