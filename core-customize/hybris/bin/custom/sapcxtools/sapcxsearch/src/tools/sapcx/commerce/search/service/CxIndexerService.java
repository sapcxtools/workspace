package tools.sapcx.commerce.search.service;

import java.util.List;
import java.util.Map;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

/**
 * Cx Indexer Service
 * With custom indexer operation functions
 */
public interface CxIndexerService {

	/**
	 * Perform index Operation for given search config, index operation and list of indexed types.
	 *
	 * @param facetSearchConfig - Facet Search Config which should be used.
	 * @param indexOperation - Indexer Operation which should be performed
	 * @param indexedTypeNameList - List of indexed types which should be indexed.
	 * @throws IndexerException
	 */
	void performIndexForIndexedTypes(FacetSearchConfig facetSearchConfig, IndexOperation indexOperation, List<String> indexedTypeNameList) throws IndexerException;

	/**
	 * Perform index Operation for given search config, index operation, list of indexed types and map of hints.
	 *
	 * @param facetSearchConfig - Facet Search Config which should be used.
	 * @param indexOperation - Indexer Operation which should be performed
	 * @param indexedTypeNameList - List of indexed types which should be indexed.
	 * @param indexerHints - Map of indexer hints which should be included.
	 * @throws IndexerException
	 */
	void performIndexForIndexedTypes(FacetSearchConfig facetSearchConfig, IndexOperation indexOperation, List<String> indexedTypeNameList, Map<String, String> indexerHints)
			throws IndexerException;

}
