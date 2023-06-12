package tools.sapcx.commerce.search.service;

import java.util.List;
import java.util.Map;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

/**
 * Cx Indexer Service with custom indexer operation functions
 */
public interface CxIndexerService {
	/**
	 * Perform index operation for given search config, index operation and a list of indexed types.
	 *
	 * @param facetSearchConfig   facet search config to use
	 * @param indexOperation      index operation to perform
	 * @param indexedTypeNameList list of indexed types to be indexed
	 * @throws IndexerException
	 */
	void performIndexForIndexedTypes(
			FacetSearchConfig facetSearchConfig,
			IndexOperation indexOperation,
			List<String> indexedTypeNameList) throws IndexerException;

	/**
	 * Perform index Operation for given search config, index operation, list of indexed types and map of hints.
	 *
	 * @param facetSearchConfig   facet search config to use
	 * @param indexOperation      index operation to perform
	 * @param indexedTypeNameList list of indexed types to be indexed
	 * @param indexerHints        map of indexer hints to be included
	 * @throws IndexerException
	 */
	void performIndexForIndexedTypes(
			FacetSearchConfig facetSearchConfig,
			IndexOperation indexOperation,
			List<String> indexedTypeNameList,
			Map<String, String> indexerHints) throws IndexerException;
}
