package sap.commerce.cx.core.registration.service;

import java.util.Map;

/**
 * Service interface for handling customer registration related functionality.
 * <p>
 * Provides operations for preparing and sending registration emails
 * using predefined templates and dynamic template parameters.
 * </p>
 */
public interface CxRegistrationService {

	/**
	 * Sends a registration email using the configured registration email template.
	 * <p>
	 * The provided parameters are used as template context values
	 * and may contain metadata such as the email subject,
	 * recipient information, or activation data.
	 * </p>
	 *
	 * @param params the template and mail parameters
	 */
	void sendRegistrationMail(final Map<String, Object> params);
}
