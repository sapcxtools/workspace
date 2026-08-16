package sap.commerce.cx.core.solr.populator;

import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.RANGE;
import static sap.commerce.cx.core.enums.SolrIndexedPropertyFacetDisplayType.SLIDER;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.spockframework.util.Pair;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryFacetsPopulator;
import de.hybris.platform.solrfacetsearch.solr.IndexedPropertyTypeInfo;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrValueFormatUtils;

/**
 * Extension of {@link FacetSearchQueryFacetsPopulator} that provides
 * custom formatting support for ranged facets.
 * <p>
 * The default implementation treats a range expression such as
 * {@code [100 TO 500]} as a single value and therefore does not apply
 * type-specific formatting correctly.
 * <p>
 * This implementation resolves the issue by:
 * <ul>
 *     <li>splitting the range into lower and upper bounds,</li>
 *     <li>formatting each bound individually using the indexed property type,</li>
 *     <li>escaping the values for Solr query syntax,</li>
 *     <li>reconstructing the formatted range expression.</li>
 * </ul>
 * <p>
 * The custom handling is applied only to facet display types that support
 * ranged filtering, such as {@code RANGE} and {@code SLIDER}.
 */
public class SearchQueryRangedFacetPopulator extends FacetSearchQueryFacetsPopulator {

	private static final String PATTERN = "\\[(\\w+)\\sTO\\s(\\w+)\\]";
	private static final String OUTPUT_FORMAT = "[%s TO %s]";
	private static final Set<String> RANGE_FACET_TYPES = Set.of(RANGE.getCode(), SLIDER.getCode());

	/**
	 * Formats and escapes a facet value for use in a Solr query.
	 * <p>
	 * For range-based facets:
	 * <ol>
	 *     <li>The range is split into lower and upper bounds.</li>
	 *     <li>Each bound is formatted individually using the indexed property type.</li>
	 *     <li>Each formatted value is escaped for Solr syntax.</li>
	 *     <li>The formatted values are recombined into a Solr range expression.</li>
	 * </ol>
	 * <p>
	 * For non-range facets, the standard formatter logic is used.
	 *
	 * @param indexedType
	 *           the indexed type configuration
	 * @param field
	 *           the indexed field name
	 * @param value
	 *           the facet value to format
	 * @return the formatted and escaped value
	 */
	@Override
	protected String formatAndEscapeValue(final IndexedType indexedType, final String field, final String value) {
		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(field);

		// Only process fields without predefined value range sets
		if (indexedProperty != null && MapUtils.isEmpty(indexedProperty.getValueRangeSets())) {
			final IndexedPropertyTypeInfo indexedPropertyTypeInfo = this.getSolrIndexedPropertyTypeRegistry()
					.getIndexPropertyTypeInfo(indexedProperty.getType());

			if (indexedPropertyTypeInfo != null) {
				if (RANGE_FACET_TYPES.contains(indexedProperty.getFacetDisplayType())) {
					// Split range: "[100 TO 500]" -> Pair("100", "500")
					final Pair<String, String> rangeValues = splitRangeValues(value);
					// Format each value individually
					final String formattedValueFrom = callStandardFormatter(rangeValues.first(), indexedPropertyTypeInfo);
					final String formattedValueTo = callStandardFormatter(rangeValues.second(), indexedPropertyTypeInfo);
					// Reconstruct as formatted range: "[formattedMin TO formattedMax]"
					return String.format(OUTPUT_FORMAT, formattedValueFrom, formattedValueTo);
				} else {
					// Non-range facet: use standard formatter
					return callStandardFormatter(value, indexedPropertyTypeInfo);
				}
			}
		}
		// Fallback: escape the value without type-specific formatting
		return ClientUtils.escapeQueryChars(value);
	}

	/**
	 * Splits a ranged facet expression into lower and upper bounds.
	 * <p>
	 * Example:
	 * <pre>
	 * [100 TO 500] -> ("100", "500")
	 * </pre>
	 *
	 * @param value
	 *           the ranged facet expression
	 * @return a {@link Pair} containing the lower and upper bounds
	 * @throws IllegalArgumentException
	 *            if the value does not match the expected range format
	 */
	private Pair<String, String> splitRangeValues(final String value) {
		final Matcher matcher = Pattern.compile(PATTERN).matcher(value);
		if (!matcher.find()) {
			throw new IllegalArgumentException("Value has wrong format: expected '[min TO max]', got " + value);
		}
		// Group 1: min value, Group 2: max value
		return Pair.of(matcher.group(1), matcher.group(2));
	}

	/**
	 * Formats a single value using the indexed property type formatter
	 * and escapes it for Solr query syntax.
	 *
	 * @param value
	 *           the value to format
	 * @param indexedPropertyTypeInfo
	 *           metadata describing the indexed property type
	 * @return the formatted and escaped value
	 */
	private String callStandardFormatter(final String value, final IndexedPropertyTypeInfo indexedPropertyTypeInfo) {
		// Format the value according to its type (e.g., date, number, decimal)
		final String formattedValue = SolrValueFormatUtils.format(value, indexedPropertyTypeInfo.getJavaType());
		// Escape special characters for Solr query syntax
		return ClientUtils.escapeQueryChars(formattedValue);
	}
}
