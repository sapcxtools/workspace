package sap.commerce.cx.core.contentsearch;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;

/**
 * Defines how content search requests are executed and mapped to paged result data.
 *
 * @param <STATE>  the state type used by the search layer
 * @param <RESULT> the concrete result page type returned by the strategy
 */
public interface ContentSearchStrategy<STATE, RESULT extends de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData<STATE, RESULT>> {
	/**
	 * Performs a content search using the given query state and paging definition.
	 *
	 * @param searchStateData the search state including the query definition
	 * @param pageableData    the requested page, size, and sorting setup
	 * @return a faceted search page containing the matching results
	 */
	ContentSearchPageData<STATE, RESULT> search(SearchStateData searchStateData, PageableData pageableData);

	/**
	 * Executes a paged content search.
	 *
	 * @param filters     Filter conditions that combine attribute keys and attribute values.
	 * @param query       encoded search term
	 * @param currentPage the current page number
	 * @param pageSize    the number of items per page
	 * @param sort        the sorting criteria
	 * @return paged search result
	 */
	ContentSearchPageData<STATE, RESULT> search(String filters, String query, int currentPage, int pageSize, String sort);
}
