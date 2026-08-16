package sap.commerce.cx.core.solr.populator;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.RANGE;
import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.SLIDER;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.spockframework.util.Pair;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

/**
 * Populator that enhances range facets with statistical data and boundary values.
 */
public class SearchResponseFacetRangeValuePopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

	private static final String PATTERN = "\\[(\\w+)\\sTO\\s(\\w+)\\]";
	private static final Set<String> RANGE_FACET_TYPES = Set.of(RANGE.getCode(), SLIDER.getCode());

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetSearchPageData<SolrSearchQueryData, ITEM> target) throws ConversionException {
		final IndexedType indexedType = source.getRequest().getIndexedType();
		final List<FacetData<SolrSearchQueryData>> facets = target.getFacets();

		if (isNotEmpty(facets)) {
			for (final FacetData<SolrSearchQueryData> facet : facets) {
				setType(indexedType, facet);

				// For range facets, populate additional data (stats and boundaries)
				if (isRangeFacet(indexedType, facet)) {
					setSolrStats(source, facet);
					setBoundaryValues(source, facet);
				}
			}
		}
	}

	private void setType(final IndexedType indexedType, final FacetData<SolrSearchQueryData> facet) {
		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getCode());
		if (indexedProperty != null) {
			facet.setType(indexedProperty.getType());
		}
	}

	private boolean isRangeFacet(final IndexedType indexedType, final FacetData<SolrSearchQueryData> facet) {
		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getCode());
		return indexedProperty != null && RANGE_FACET_TYPES.contains(indexedProperty.getFacetDisplayType());
	}

	private void setSolrStats(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetData<SolrSearchQueryData> facet) {
		if (source.getSearchResult() instanceof final ExtendedSolrSearchResult searchResult) {
			if (searchResult.getSolrStats() != null && searchResult.getSolrStats().getSolrStatsFields() != null) {
				facet.setSolrStatsField(searchResult.getSolrStats().getSolrStatsFields().get(facet.getCode()));
			}
		}
	}

	private void setBoundaryValues(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetData<SolrSearchQueryData> facet) {
		emptyIfNull(source.getRequest().getSearchQueryData().getFilterTerms()).stream()
				.filter(term -> StringUtils.equals(term.getKey(), facet.getCode())).findFirst()
				.map(SolrSearchQueryTermData::getValue)
				// Extract min and max from the range value (e.g., "[100 TO 500]")
				.map(this::splitRangeValues).ifPresent(pair -> {
					facet.setFrom(pair.first());
					facet.setTo(pair.second());
				});
		if (facet.getFrom() != null && facet.getTo() != null) {
			facet.getValues().stream().findFirst().ifPresent(facetValue -> facetValue.setSelected(true));
		}
	}

	private Pair<String, String> splitRangeValues(final String value) {
		final Matcher matcher = Pattern.compile(PATTERN).matcher(value);
		if (!matcher.find()) {
			throw new IllegalArgumentException("Value has wrong format: expected '[min TO max]', got " + value);
		}
		return Pair.of(matcher.group(1), matcher.group(2));
	}
}
