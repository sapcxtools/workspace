package sap.commerce.cx.facades.solr.populator;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populator that maps a facet's "remove query" from the source facet to the target facet.
 */
public class FacetDataRemoveQueryPopulator<QUERY, STATE> implements Populator<FacetData<QUERY>, FacetData<STATE>> {
	private final Converter<QUERY, STATE> searchStateConverter;

	public FacetDataRemoveQueryPopulator(final Converter<QUERY, STATE> searchStateConverter) {
		this.searchStateConverter = searchStateConverter;
	}

	@Override
	public void populate(final FacetData<QUERY> source, final FacetData<STATE> target) throws ConversionException {
		if (source.getRemoveQuery() != null) {
			target.setRemoveQuery(searchStateConverter.convert(source.getRemoveQuery()));
		}
	}
}
