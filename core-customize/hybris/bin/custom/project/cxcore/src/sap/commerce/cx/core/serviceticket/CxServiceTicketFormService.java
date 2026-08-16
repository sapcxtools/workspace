package sap.commerce.cx.core.serviceticket;

import java.util.List;
import java.util.Optional;

import sap.commerce.cx.core.model.CxServiceTicketFormModel;

/**
 * Service API for loading configured service request forms.
 */
public interface CxServiceTicketFormService {

	/**
	 * Returns all configured service request forms.
	 *
	 * @return all service request forms
	 */
	List<CxServiceTicketFormModel> getAllServiceRequestForms();

	/**
	 * Returns a service request form by its identifier.
	 *
	 * @param id the form identifier
	 * @return the matching form or {@code null} if no form exists
	 */
	Optional<CxServiceTicketFormModel> getServiceRequestFormForId(String id);

}
