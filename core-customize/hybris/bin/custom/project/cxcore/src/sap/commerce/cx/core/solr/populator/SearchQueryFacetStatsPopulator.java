package sap.commerce.cx.core.solr.populator;

import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.RANGE;
import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.SLIDER;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

/**
 * Populator responsible for adding Solr field statistics requests
 * to a {@link SolrQuery} for range-based facets.
 * <p>
 * Statistics are only added for facet display types that support range-based filtering, such as {@code RANGE} and {@code SLIDER}.
 * The collected statistics can be used to display accurate minimum and maximum bounds in the storefront.
 * <p>
 * When applicable, the populator also excludes active filter queries from the statistics calculation by using Solr local parameter tags.
 */
public class SearchQueryFacetStatsPopulator implements Populator<SearchQueryConverterData, SolrQuery> {

	private static final String REGEX_TAGNAME_PREFIX = "^(\\{.*\\!tag=(f[0-9]+).*\\})?";
	private static final String REGEX_TAGNAME_SUFFIX = ":.*$";
	private static final Set<String> RANGE_FACET_TYPES = Set.of(RANGE.getCode(), SLIDER.getCode());

	private final FieldNameTranslator fieldNameTranslator;

	public SearchQueryFacetStatsPopulator(final FieldNameTranslator fieldNameTranslator) {
		this.fieldNameTranslator = fieldNameTranslator;
	}

	@Override
	public void populate(final SearchQueryConverterData source, final SolrQuery target) {
		final SearchQuery searchQuery = source.getSearchQuery();

		// Iterate through all facets to find range facets, other facet types (checkbox, radio, select) don't benefit
		// from stats
		for (final FacetField facet : searchQuery.getFacets()) {
			final IndexedProperty indexedProperty = getIndexedProperty(searchQuery.getIndexedType(), facet.getField());

			if (indexedProperty != null && RANGE_FACET_TYPES.contains(indexedProperty.getFacetDisplayType())) {
				final String translatedField = fieldNameTranslator.translate(searchQuery, facet.getField(),
						FieldNameProvider.FieldType.INDEX);
				final String tagName = getTagNameFromFilterQuery(target, translatedField);

				if (StringUtils.isNoneBlank(tagName)) {
					target.addGetFieldStatistics("{!ex=" + tagName + "}" + translatedField);
				} else {
					target.addGetFieldStatistics(translatedField);
				}
			}
		}
	}

	private IndexedProperty getIndexedProperty(final IndexedType indexedType, final String field)
			throws UnknownIdentifierException {
		return indexedType.getIndexedProperties()
				.entrySet()
				.stream()
				.filter(entry -> field.equals(entry.getKey()))
				.findFirst()
				.map(Map.Entry::getValue)
				.orElse(null);
	}

	private String getTagNameFromFilterQuery(final SolrQuery solrQuery, final String fieldName) {
		if (solrQuery == null || solrQuery.getFilterQueries() == null) {
			return null;
		}

		return Arrays.stream(solrQuery.getFilterQueries())
				.filter(term -> term.contains(fieldName))
				.map(term -> fetchTagNameFromTerm(term, fieldName))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

	private String fetchTagNameFromTerm(final String term, final String fieldName) {
		final Pattern pattern = Pattern.compile(getRegex(fieldName));
		final Matcher matcher = pattern.matcher(term);
		return matcher.matches() ? matcher.group(2) : null;
	}

	private String getRegex(final String fieldName) {
		return REGEX_TAGNAME_PREFIX + fieldName + REGEX_TAGNAME_SUFFIX;
	}
}
