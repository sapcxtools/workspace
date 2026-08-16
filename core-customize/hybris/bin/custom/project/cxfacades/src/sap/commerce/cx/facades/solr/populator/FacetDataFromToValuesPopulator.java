package sap.commerce.cx.facades.solr.populator;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that copies 'from' and 'to' range boundary values from source facet to target facet.
 */
public class FacetDataFromToValuesPopulator<QUERY, STATE> implements Populator<FacetData<QUERY>, FacetData<STATE>> {
	@Override
	public void populate(final FacetData<QUERY> source, final FacetData<STATE> target) throws ConversionException {
		if (StringUtils.isNotEmpty(source.getFrom()) && StringUtils.isNotEmpty(source.getTo())) {
			target.setFrom(source.getFrom());
			target.setTo(source.getTo());
		}
	}
}
