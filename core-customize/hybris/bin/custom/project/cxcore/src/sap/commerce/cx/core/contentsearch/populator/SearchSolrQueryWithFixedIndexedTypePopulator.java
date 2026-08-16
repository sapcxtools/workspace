package sap.commerce.cx.core.contentsearch.populator;

import static org.apache.commons.collections4.MapUtils.isEmpty;

import java.util.Collection;
import java.util.Map;

import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSolrQueryPopulator;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

/**
 * Solr query populator that always resolves a configured indexed type identifier.
 *
 * @param <INDEXED_PROPERTY_TYPE>  the indexed property type
 * @param <INDEXED_TYPE_SORT_TYPE> the sort type for indexed types
 */
public class SearchSolrQueryWithFixedIndexedTypePopulator<INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE>
		extends SearchSolrQueryPopulator<INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE> {
	private final String indexedTypeIdentifier;

	public SearchSolrQueryWithFixedIndexedTypePopulator(final String indexedTypeIdentifier) {
		this.indexedTypeIdentifier = indexedTypeIdentifier;
	}

	@Override
	protected IndexedType getIndexedType(final FacetSearchConfig config) {
		if (config == null || config.getIndexConfig() == null) {
			// Return null for invalid configurations
			return null;
		}

		final Map<String, IndexedType> indexedTypes1 = config.getIndexConfig().getIndexedTypes();
		if (isEmpty(indexedTypes1)) {
			return null;
		}

		// Strategy for working out which of the available indexed types to use
		final Collection<IndexedType> indexedTypes = indexedTypes1.values();
		return indexedTypes.stream()
				.filter(indexedType -> indexedTypeIdentifier.equals(indexedType.getIdentifier()))
				.findFirst()
				.orElse(null);
	}
}
