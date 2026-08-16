package sap.commerce.cx.core.solr.populator;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

/**
 * Populator that adds facet display type information to search results.
 */
public class SearchResponseFacetDisplayTypePopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetSearchPageData<SolrSearchQueryData, ITEM> target) {
		final IndexedType indexedType = source.getRequest().getIndexedType();

		final List<FacetData<SolrSearchQueryData>> facets = target.getFacets();
		for (final FacetData<SolrSearchQueryData> facet : emptyIfNull(facets)) {
			final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getCode());

			// Only set display type if it's configured (not null)
			if (indexedProperty != null && indexedProperty.getFacetDisplayType() != null) {
				facet.setFacetDisplayType(indexedProperty.getFacetDisplayType());
			}
		}
	}
}
