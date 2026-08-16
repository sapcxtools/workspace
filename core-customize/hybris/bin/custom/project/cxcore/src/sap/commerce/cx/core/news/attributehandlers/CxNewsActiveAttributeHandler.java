package sap.commerce.cx.core.news.attributehandlers;

import java.util.Date;
import java.util.Objects;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.time.TimeService;

import sap.commerce.cx.core.model.CxNewsModel;

/**
 * Dynamic attribute handler that determines whether a {@link CxNewsModel}
 * is currently active and should be displayed.
 * <p>
 * A news entry is considered active if:
 * <ul>
 *     <li>the current time is after (or no {@code validFrom} date is defined), and</li>
 *     <li>the current time is before (or no {@code validTo} date is defined).</li>
 * </ul>
 * If both {@code validFrom} and {@code validTo} are {@code null}, the news
 * entry is always considered active.
 * </p>
 */
public class CxNewsActiveAttributeHandler implements DynamicAttributeHandler<Boolean, CxNewsModel> {
	private final TimeService timeService;

	/**
	 * Creates a new instance.
	 *
	 * @param timeService used to obtain the current time; must not be {@code null}
	 * @throws NullPointerException if {@code timeService} is {@code null}
	 */
	public CxNewsActiveAttributeHandler(final TimeService timeService) {
		this.timeService = Objects.requireNonNull(timeService, "timeService must not be null");
	}

	/**
	 * Computes whether the news entry is currently active.
	 *
	 * @param model the news model to evaluate; must not be {@code null}
	 * @return {@code true} if the current time falls within the validity window,
	 *         {@code false} otherwise
	 */
	@Override
	public Boolean get(final CxNewsModel model) {
		final Date now = timeService.getCurrentTime();

		final Date validFrom = model.getValidFrom();
		final Date validTo = model.getValidTo();

		final boolean startIsEmptyOrReached = validFrom == null || now.after(validFrom);
		final boolean endIsEmptyOrNotReached = validTo == null || now.before(validTo);

		return startIsEmptyOrReached && endIsEmptyOrNotReached;
	}

	/**
	 * Not supported — {@code active} is a read-only dynamic attribute.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void set(final CxNewsModel model, final Boolean value) {
		throw new UnsupportedOperationException(
				"CxNews.active is calculated dynamically and cannot be set.");
	}
}
