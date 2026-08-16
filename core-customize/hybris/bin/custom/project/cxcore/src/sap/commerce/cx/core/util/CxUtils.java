package sap.commerce.cx.core.util;

import java.util.Locale;

import com.google.common.annotations.VisibleForTesting;

import de.hybris.platform.util.localization.Localization;

/**
 * Utility methods for common operations.
 */
public class CxUtils {
	/**
	 * Converts the given value to lower case using {@link Locale#ROOT}.
	 *
	 * @param value the value to convert
	 * @return the lower-case value or an empty string if {@code value} is {@code null}
	 */
	public static String safeLowerCase(final String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}

	/**
	 * Resolves a localized message without arguments.
	 *
	 * @param keyPrefix prefix of the localization key
	 * @param id        identifier appended to the prefix
	 * @return localized message or fallback value provided by the localization service
	 */
	@VisibleForTesting
	public static String getLocalizedString(final String keyPrefix, final String id) {
		return getLocalizedString(keyPrefix, id, new Object[0]);
	}

	/**
	 * Resolves a localized message using the composed key {@code keyPrefix + id} and formats it with the supplied arguments.
	 *
	 * @param keyPrefix prefix of the localization key
	 * @param id        identifier appended to the prefix
	 * @param arguments optional message format arguments
	 * @return localized and formatted message
	 */
	public static String getLocalizedString(final String keyPrefix, final String id, final Object... arguments) {
		final Object[] safeArguments = arguments == null ? new Object[0] : arguments;
		return Localization.getLocalizedString(keyPrefix + id, safeArguments);
	}
}
