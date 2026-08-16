package sap.commerce.cx.core.serviceticket;

import java.util.Collection;
import java.util.List;

import jakarta.mail.internet.InternetAddress;

/**
 * Helper for converting raw email address strings to valid internet addresses.
 */
public interface CxEmailServiceHelper {

	/**
	 * Converts the provided recipient list to a collection of {@link InternetAddress} instances.
	 *
	 * @param addressList the raw address values to convert
	 * @return converted internet addresses; invalid values are skipped
	 */
	Collection<InternetAddress> convertToInternetAddressCollection(List<String> addressList);
}
