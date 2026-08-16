package sap.commerce.cx.core.solr.populator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.FieldStatsInfo;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.impl.SearchResultConverterData;

import sap.commerce.cx.core.solr.SolrStats;
import sap.commerce.cx.core.solr.SolrStatsField;

/**
 * Populator that converts Solr field statistics to custom SolrStats objects.
 */
public class SearchResultFacetStatsPopulator implements Populator<SearchResultConverterData, ExtendedSolrSearchResult> {
	private static final Logger LOG = Logger.getLogger(SearchResultFacetStatsPopulator.class);
	private final FieldNameTranslator fieldNameTranslator;

	public SearchResultFacetStatsPopulator(final FieldNameTranslator fieldNameTranslator) {
		this.fieldNameTranslator = fieldNameTranslator;
	}

	@Override
	public void populate(final SearchResultConverterData source, final ExtendedSolrSearchResult target)
			throws ConversionException {
		final Map<String, FieldStatsInfo> statsInfo = source.getQueryResponse().getFieldStatsInfo();

		if (MapUtils.isNotEmpty(statsInfo)) {
			final FieldNameTranslator.FieldInfosMapping fieldInfosMapping = fieldNameTranslator.getFieldInfos(
					source.getFacetSearchContext());

			final Map<String, SolrStatsField> solrStatsFields = statsInfo.values().stream()
					.map(fieldStatsInfo -> convertToSolrStatsField(fieldStatsInfo, fieldInfosMapping))
					.collect(Collectors.toMap(SolrStatsField::getName, Function.identity()));

			final SolrStats solrStats = new SolrStats();
			solrStats.setSolrStatsFields(solrStatsFields);
			target.setSolrStats(solrStats);
		}
	}

	private SolrStatsField convertToSolrStatsField(final FieldStatsInfo fieldStatsInfo,
			final FieldNameTranslator.FieldInfosMapping fieldInfosMapping) {
		final String translatedName = fieldStatsInfo.getName();
		final FieldNameTranslator.FieldInfo fieldInfo = fieldInfosMapping.getInvertedFieldInfos().get(translatedName);

		final SolrStatsField solrStatsField = new SolrStatsField();
		if (fieldInfo != null) {
			solrStatsField.setName(fieldInfo.getFieldName());
		} else {
			LOG.warn("Could not find IndexedProperty for stats field " + translatedName
					+ ". This is unexpected behaviour. Using translated name as fallback.");
			solrStatsField.setName(translatedName);
		}

		solrStatsField.setMin((Double) fieldStatsInfo.getMin());
		solrStatsField.setMax((Double) fieldStatsInfo.getMax());
		solrStatsField.setCount(fieldStatsInfo.getCount());
		solrStatsField.setCountDistinct(fieldStatsInfo.getCountDistinct());
		solrStatsField.setMissing(fieldStatsInfo.getMissing());
		solrStatsField.setSum((Double) fieldStatsInfo.getSum());
		solrStatsField.setSumOfSquares(fieldStatsInfo.getSumOfSquares());
		solrStatsField.setMean((Double) fieldStatsInfo.getMean());
		solrStatsField.setStdDev(fieldStatsInfo.getStddev());

		return solrStatsField;
	}
}
