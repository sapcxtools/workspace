package sap.commerce.cx.core.news.service;

import java.util.List;

import sap.commerce.cx.core.model.CxNewsModel;

/**
 * Service for retrieving CX news entries.
 */
public interface CxNewsService {
	/**
	 * Returns the latest active news entries, limited to the count configured
	 * via the {@code cx.news.component.maxItems} property. Falls back to 5
	 * if the property is not set.
	 *
	 * @return list of active {@link CxNewsModel} instances, never {@code null}
	 */
	List<CxNewsModel> getLatestActiveNews();

	/**
	 * Returns the latest active news entries, limited to {@code count} items.
	 *
	 * @param count maximum number of entries to return; values &lt;= 0 fall back
	 *              to the default.
	 * @return list of active {@link CxNewsModel} instances, never {@code null}
	 */
	List<CxNewsModel> getLatestActiveNews(int count);

	List<CxNewsModel> getAllActiveNews();
}
