package sap.commerce.cx.facades.solr.populator;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that copies the facet 'type' attribute from source to target facet data.
 */
public class FacetDataTypePopulator<QUERY, STATE> implements Populator<FacetData<QUERY>, FacetData<STATE>> {
	@Override
	public void populate(final FacetData<QUERY> source, final FacetData<STATE> target) throws ConversionException {
		if (StringUtils.isNotEmpty(source.getType())) {
			target.setType(source.getType());
		}
	}
}
