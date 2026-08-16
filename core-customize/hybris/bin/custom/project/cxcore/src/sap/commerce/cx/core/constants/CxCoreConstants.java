package sap.commerce.cx.core.constants;

/**
 * Global constants used within the CX Core extension.
 * <p>
 * This class contains grouped constant definitions to ensure
 * centralized management of configuration keys, codes, and
 * other static application values.
 * </p>
 */
@SuppressWarnings({ "deprecation", "squid:CallToDeprecatedMethod" })
public final class CxCoreConstants extends GeneratedCxCoreConstants {
	public static final String EXTENSIONNAME = "cxcore";

	private CxCoreConstants() {
		// empty to avoid instantiating this constant class
	}

	/**
	 * Constants related to email configuration and processing.
	 */
	public static class EMAIL {
		public static final String EMAIL_REPLY_TO = "mail.replyto";
		public static final String EMAIL_CONTACT_TEMPLATE = "cx.email.contact.template";
		public static final String EMAIL_REGISTRATION_TEMPLATE = "cx.email.registration.template";
		public static final String EMAIL_REGISTRATION_SUBJECT = "cx.email.registration.subject";
		public static final String EMAIL_SERVICE_TICKET_TEMPLATE = "cx.email.serviceticket.template";
		public static final String EMAIL_SERVICE_TICKET_SUBJECT_PREFIX = "cx.email.serviceticket.subject.";
		public static final String EMAIL_SERVICE_TICKET_INTRO_PREFIX = "cx.email.serviceticket.intro.";
	}

	/**
	 * Constants related to dashboard configuration.
	 */
	public static class DASHBOARD {
		public static final String DEFAULT_CONFIG_CODE = "defaultDashboardConfig";
		public static final String CUSTOM_CONFIG_PREFIX = "customConfig";
		public static final String CUSTOM_CONFIG_PATTERN = "%s_%s_%s";
	}
}
