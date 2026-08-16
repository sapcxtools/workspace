package sap.commerce.cx.core.solr.populator;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

/**
 * Populator that adds a "remove query" to each facet.
 */
public class SearchResponseFacetRemoveQueryPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetSearchPageData<SolrSearchQueryData, ITEM> target) {

		final List<FacetData<SolrSearchQueryData>> facets = target.getFacets();
		for (final FacetData<SolrSearchQueryData> facet : emptyIfNull(facets)) {
			facet.setRemoveQuery(refineQueryRemoveFacet(target.getCurrentQuery(), facet.getCode()));
		}
	}

	private SolrSearchQueryData refineQueryRemoveFacet(final SolrSearchQueryData searchQueryData,
			final String facetCode) {
		final List<SolrSearchQueryTermData> newTerms = new ArrayList<>(searchQueryData.getFilterTerms());

		newTerms.removeIf(term -> facetCode.equals(term.getKey()));

		final SolrSearchQueryData result = cloneSearchQueryData(searchQueryData);
		result.setFilterTerms(newTerms);
		return result;
	}

	private SolrSearchQueryData cloneSearchQueryData(final SolrSearchQueryData source) {
		final SolrSearchQueryData target = new SolrSearchQueryData();

		target.setFreeTextSearch(source.getFreeTextSearch());
		target.setCategoryCode(source.getCategoryCode());
		target.setSort(source.getSort());

		if (source.getFilterTerms() != null) {
			target.setFilterTerms(new ArrayList<>(source.getFilterTerms()));
		}
		if (source.getFilterQueries() != null) {
			target.setFilterQueries(new ArrayList<>(source.getFilterQueries()));
		}

		return target;
	}
}
