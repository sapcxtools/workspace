package sap.commerce.cx.core.serviceticket.exception;

import jakarta.servlet.ServletException;

/**
 * Exception wrapper used for service ticket specific failures.
 */
public class ServiceTicketException extends ServletException {
	private final Exception exception;
	private final String message;
	private final String type;

	/**
	 * Creates a new service ticket exception.
	 *
	 * @param exception the original exception
	 * @param message   the business error message
	 * @param type      the error type identifier
	 */
	public ServiceTicketException(final Exception exception, final String message, final String type) {
		super("Service ticket error. : " + message, exception);
		this.exception = exception;
		this.message = message;
		this.type = type;
	}

	/**
	 * Returns the original exception.
	 *
	 * @return wrapped exception
	 */
	public Exception getException() {
		return exception;
	}

	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the business error type.
	 *
	 * @return error type identifier
	 */
	public String getType() {
		return type;
	}
}
