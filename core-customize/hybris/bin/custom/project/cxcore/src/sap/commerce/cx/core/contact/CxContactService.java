package sap.commerce.cx.core.contact;

import java.util.Map;

/**
 * Service interface for handling contact-related functionality.
 * <p>
 * Provides operations for preparing and sending contact form emails
 * using predefined templates and dynamic template parameters.
 * </p>
 */
public interface CxContactService {

	/**
	 * Sends a contact email using the configured contact email template.
	 *
	 * @param params the template and mail parameters
	 */
	void sendContactMail(final Map<String, Object> params);
}
