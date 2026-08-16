package sap.commerce.cx.core.solr.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;

/**
 * Populator that converts the facet display type from a SolrIndexedPropertyModel to an IndexedProperty.
 */
public class IndexedPropertyFacetDisplayTypePopulator implements Populator<SolrIndexedPropertyModel, IndexedProperty> {

	/**
	 * Populates the target {@link IndexedProperty} with the facet display type defined in the source
	 * {@link SolrIndexedPropertyModel}.
	 *
	 * @param source the source indexed property model
	 * @param target the target indexed property configuration
	 * @throws ConversionException if an error occurs during population
	 */
	@Override
	public void populate(final SolrIndexedPropertyModel source, final IndexedProperty target)
			throws ConversionException {
		if (source.getFacetDisplayType() != null) {
			target.setFacetDisplayType(source.getFacetDisplayType().getCode());
		}
	}
}
