package sap.commerce.cx.facades.registration;

import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;

/**
 * Facade interface for handling CX registration requests.
 * <p>
 * Provides operations for processing registration data submitted
 * through the storefront or OCC layer.
 * </p>
 */
public interface CxRegistrationFacade {
	/**
	 * Processes the given registration data.
	 *
	 * @param data the registration data
	 */
	void register(B2BRegistrationData data);
}
