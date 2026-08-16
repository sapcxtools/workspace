package sap.commerce.cx.core.contentsearch.impl;

import static java.util.Objects.requireNonNull;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.*;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.contentsearch.ContentSearchStrategy;
import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;
import sap.commerce.cx.core.solr.util.SearchQueryCodec;

/**
 * Default {@link ContentSearchStrategy} implementation that translates storefront search state into Solr requests and
 * converts Solr responses back to {@link ContentSearchPageData}.
 *
 * @param <STATE>  the type used to represent the search state
 * @param <RESULT> the concrete page data type returned by the search
 */
public class DefaultContentSearchStrategy<STATE, RESULT extends de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData<STATE, RESULT>>
		implements ContentSearchStrategy<STATE, RESULT> {
	private static final int NEXT_FILTER = 2;

	private final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter;
	private final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;
	private final Converter<SolrSearchResponse, ContentSearchPageData<STATE, RESULT>> searchResponseConverter;
	private final Converter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder;
	private final ThreadContextService threadContextService;
	private final SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	private final Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
	/**
	 * Creates the strategy with all required Solr converters and thread context handling.
	 *
	 * @param searchRequestConverter       converts a built Solr request into a Solr response
	 * @param searchResponseConverter      converts a Solr response to content search page data
	 * @param solrSearchQueryDecoder       decodes storefront query data to Solr query data
	 * @param threadContextService         executes the search in the current thread context
	 * @param searchQueryPageableConverter converts query and paging data into a Solr request
	 */
	public DefaultContentSearchStrategy(final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter,
			final Converter<SolrSearchResponse, ContentSearchPageData<STATE, RESULT>> searchResponseConverter,
			final Converter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder,
			final ThreadContextService threadContextService,
			final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter,
			final SearchQueryCodec<SolrSearchQueryData> searchQueryCodec,
			final Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter) {
		this.searchRequestConverter = requireNonNull(searchRequestConverter);
		this.searchResponseConverter = requireNonNull(searchResponseConverter);
		this.solrSearchQueryDecoder = requireNonNull(solrSearchQueryDecoder);
		this.threadContextService = requireNonNull(threadContextService);
		this.searchQueryPageableConverter = requireNonNull(searchQueryPageableConverter);
		this.searchQueryCodec = requireNonNull(searchQueryCodec);
		this.solrSearchStateConverter = requireNonNull(solrSearchStateConverter);
	}

	/**
	 * Runs the search for the given state and paging configuration.
	 *
	 * @param searchState search state that contains the encoded query
	 * @param pageableData    pagination and sorting information
	 * @return search page data produced from the Solr response
	 */
	@Override
	public ContentSearchPageData<STATE, RESULT> search(final SearchStateData searchState,
			final PageableData pageableData) {
		final SolrSearchQueryData searchQueryData = solrSearchQueryDecoder.convert(searchState.getQuery());
		return threadContextService.executeInContext(() -> doSearch(searchQueryData, pageableData));
	}

	@Override
	public ContentSearchPageData<STATE, RESULT> search(final String filters, final String query, final int currentPage, final int pageSize, final String sort) {
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		searchQueryData.setFilterQueries(decodeFilters(filters));
		final SearchStateData searchStateData = solrSearchStateConverter.convert(searchQueryData);
		if (searchStateData == null) {
			return null;
		}
		final PageableData pageableData = createPagableData(currentPage, pageSize, sort);

		return search(searchStateData, pageableData);
	}

	private ContentSearchPageData<STATE, RESULT> doSearch(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData) {
		final SolrSearchRequest solrSearchRequest = searchQueryPageableConverter.convert(
				getSearchQueryPageableData(searchQueryData, pageableData));

		final SolrSearchResponse solrSearchResponse = searchRequestConverter.convert(solrSearchRequest);

		return searchResponseConverter.convert(solrSearchResponse);
	}

	private SearchQueryPageableData<SolrSearchQueryData> getSearchQueryPageableData(
			final SolrSearchQueryData searchQueryData, final PageableData pageableData) {
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = new SearchQueryPageableData<>();
		searchQueryPageableData.setSearchQueryData(searchQueryData);
		searchQueryPageableData.setPageableData(pageableData);
		return searchQueryPageableData;
	}

	protected PageableData createPagableData(final int currentPage, final int pageSize, final String sort) {
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		return pageableData;
	}

	protected List<SolrSearchFilterQueryData> decodeFilters(final String filters) {
		if (StringUtils.isBlank(filters)) {
			return Collections.emptyList();
		}

		final String[] parts = filters.split(":", -1);
		if (parts.length % NEXT_FILTER != 0) {
			throw new RequestParameterException(
					"Invalid filters format. You have to provide filters as " +
							"'<attributeKey1>:<attributeValue1>:...:<attributeKeyN>:<attributeValueN>'",
					RequestParameterException.INVALID);
		}
		final Map<String, Set<String>> filterMap = new LinkedHashMap<>();
		for (int i = 0; i < parts.length; i += NEXT_FILTER) {
			final String key = parts[i];
			final String valuePart = parts[i + 1];

			filterMap
					.computeIfAbsent(key, k -> new LinkedHashSet<>())
					.addAll(Arrays.asList(valuePart.split(",", -1)));
		}

		final List<SolrSearchFilterQueryData> filterQueries = new ArrayList<>(filterMap.size());
		for (final Map.Entry<String, Set<String>> entry : filterMap.entrySet()) {
			final SolrSearchFilterQueryData filter = new SolrSearchFilterQueryData();
			filter.setKey(entry.getKey());
			filter.setValues(entry.getValue());
			filter.setOperator(FilterQueryOperator.OR);
			filterQueries.add(filter);
		}

		return filterQueries;
	}
}
