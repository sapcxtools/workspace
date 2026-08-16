package sap.commerce.cx.facades.news;

import java.util.List;

import sap.commerce.cx.facades.news.data.CxNewsData;

/**
 * Facade for retrieving CX news entries as transfer objects.
 */
public interface CxNewsFacade {

	/**
	 * Returns the latest active news entries, limited to the count configured
	 * via the {@code cx.news.component.maxItems} property. Falls back to 5
	 * if the property is not set.
	 *
	 * @return list of active {@link CxNewsData} instances, never {@code null}
	 */
	List<CxNewsData> getLatestActiveNews();

	/**
	 * Returns the latest active news entries, limited to {@code count} items.
	 *
	 * @param count maximum number of entries to return; values &lt;= 0 fall back
	 *              to the configured default
	 * @return list of active {@link CxNewsData} instances, never {@code null}
	 */
	List<CxNewsData> getLatestActiveNews(int count);

	List<CxNewsData> getAllActiveNews();
}
