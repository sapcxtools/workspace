package sap.commerce.cx.facades.solr.populator;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that transfers Solr statistics field metadata from source to target facet data.
 */
public class FacetDataSolrStatsPopulator<QUERY, STATE> implements Populator<FacetData<QUERY>, FacetData<STATE>> {
	@Override
	public void populate(final FacetData<QUERY> source, final FacetData<STATE> target) throws ConversionException {
		if (source.getSolrStatsField() != null) {
			target.setSolrStatsField(source.getSolrStatsField());
		}
	}
}
