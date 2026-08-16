package sap.commerce.cx.core.serviceticket.attributehandler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import sap.commerce.cx.core.model.CxServiceTicketModel;

/**
 * Dynamic attribute handler that classifies tickets as "new" based on configurable age threshold.
 */
public class IsTicketClassifiedAsNewAttributeHandler
		implements DynamicAttributeHandler<Boolean, CxServiceTicketModel> {
	public static final int DEFAULT_NEW_DAYS = 3;
	public static final String NEW_DAYS_PROPERTY = "cx.serviceticket.new.threshold.days";

	private final ConfigurationService configurationService;

	public IsTicketClassifiedAsNewAttributeHandler(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * Evaluates whether the ticket age is within the configured "new" threshold.
	 *
	 * @param model the ticket model
	 * @return {@code true} if the ticket is considered new
	 */
	@Override
	public Boolean get(final CxServiceTicketModel model) {
		final int newDaysThreshold = configurationService.getConfiguration()
				.getInt(NEW_DAYS_PROPERTY, DEFAULT_NEW_DAYS);

		final LocalDate creationDate = model.getCreationtime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		final long ageInDays = ChronoUnit.DAYS.between(creationDate, LocalDate.now());
		return ageInDays <= newDaysThreshold;
	}

	/**
	 * This dynamic attribute is read-only.
	 *
	 * @param model the ticket model
	 * @param value ignored
	 */
	@Override
	public void set(final CxServiceTicketModel model, final Boolean value) {
		// do nothing on purpose
	}
}
