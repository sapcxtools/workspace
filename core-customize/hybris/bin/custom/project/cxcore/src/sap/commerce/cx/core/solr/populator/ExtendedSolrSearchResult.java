package sap.commerce.cx.core.solr.populator;

import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import sap.commerce.cx.core.solr.SolrStats;

/**
 * Extended implementation of {@link SolrSearchResult} that includes additional Solr statistics information.
 */
public class ExtendedSolrSearchResult extends SolrSearchResult {
	private SolrStats solrStats;

	public SolrStats getSolrStats() {
		return solrStats;
	}

	public void setSolrStats(final SolrStats solrStats) {
		this.solrStats = solrStats;
	}
}
