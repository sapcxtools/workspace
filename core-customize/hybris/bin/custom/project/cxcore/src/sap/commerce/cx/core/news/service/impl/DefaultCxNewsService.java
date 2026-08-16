package sap.commerce.cx.core.news.service.impl;

import java.util.List;
import java.util.Objects;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import sap.commerce.cx.core.model.CxNewsModel;
import sap.commerce.cx.core.news.dao.CxNewsDao;
import sap.commerce.cx.core.news.service.CxNewsService;

/**
 * Default implementation of {@link CxNewsService}.
 *
 * <p>Retrieves all news entries from {@link CxNewsDao}, filters for active ones,
 * and limits the result to a configurable maximum. The limit is read from the
 * {@code cx.news.component.maxItems} property; if absent it defaults to 5.
 */
public class DefaultCxNewsService implements CxNewsService {
	private static final String NEWS_COMPONENT_MAX_ITEMS_PROPERTY = "cx.news.component.maxItems";
	private static final int DEFAULT_MAX_ITEMS = 5;

	private final CxNewsDao cxNewsDao;
	private final ConfigurationService configurationService;

	public DefaultCxNewsService(
			final CxNewsDao cxNewsDao,
			final ConfigurationService configurationService) {
		this.cxNewsDao = Objects.requireNonNull(cxNewsDao);
		this.configurationService = Objects.requireNonNull(configurationService);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the item limit from the {@code cx.news.component.maxItems} property
	 * and delegates to {@link #getLatestActiveNews(int)}.
	 */
	@Override
	public List<CxNewsModel> getLatestActiveNews() {
		final int count = configurationService.getConfiguration()
				.getInt(NEWS_COMPONENT_MAX_ITEMS_PROPERTY, DEFAULT_MAX_ITEMS);

		return getLatestActiveNews(count);
	}

	/**
	 * Returns the latest active news entries, limited to {@code count} items.
	 *
	 * @param count maximum number of entries to return; values &lt;= 0 fall back
	 *              to the default of {@code cx.news.component.maxItems}
	 * @return list of active {@link CxNewsModel} instances, never {@code null}
	 */
	@Override
	public List<CxNewsModel> getLatestActiveNews(final int count) {
		final int sanitizedCount = sanitizeCount(count);

		return cxNewsDao.findNews().stream()
				.filter(cxNewsModel -> Boolean.TRUE.equals(cxNewsModel.getActive()))
				.limit(sanitizedCount)
				.toList();
	}

	@Override
	public List<CxNewsModel> getAllActiveNews() {
		return cxNewsDao.findNews().stream()
				.filter(cxNewsModel -> Boolean.TRUE.equals(cxNewsModel.getActive()))
				.toList();
	}

	/**
	 * Sanitizes the requested item count.
	 *
	 * <ul>
	 *   <li>Values &lt;= 0 are replaced with {@value #DEFAULT_MAX_ITEMS}.</li>
	 * </ul>
	 *
	 * @param count the raw requested count
	 * @return {@value #DEFAULT_MAX_ITEMS} if {@code count} was &lt;= 0, otherwise {@code count} unchanged
	 */
	protected int sanitizeCount(final int count) {
		if (count <= 0) {
			return DEFAULT_MAX_ITEMS;
		}
		return count;
	}
}
