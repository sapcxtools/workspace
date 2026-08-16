package sap.commerce.cx.core.serviceticket.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import sap.commerce.cx.core.model.CxServiceTicketModel;

/**
 * Provides Solr field values for machines attached to a service ticket.
 */
public class DefaultCxTicketMachinesValueProvider implements FieldValueProvider {

	private final FieldNameProvider fieldNameProvider;

	/**
	 * Creates the provider.
	 *
	 * @param fieldNameProvider provider used to resolve Solr field names
	 */
	public DefaultCxTicketMachinesValueProvider(final FieldNameProvider fieldNameProvider) {
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * Returns indexed values for machines if the input model is a service ticket.
	 *
	 * @param indexConfig     the index configuration
	 * @param indexedProperty the indexed property definition
	 * @param model           the indexed model
	 * @return field values for machine codes
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) {
		if (model instanceof final CxServiceTicketModel ticket) {
			if (CollectionUtils.isNotEmpty(ticket.getMachines())) {
				return createFieldValues(indexedProperty, ticket.getMachines());
			}
		}
		return Collections.emptyList();
	}

	protected Collection<FieldValue> createFieldValues(final IndexedProperty indexedProperty,
			final Collection<ProductModel> machines) {
		final List<FieldValue> fieldValues = new ArrayList<>();
		for (final ProductModel machine : machines) {
			addFieldValues(fieldValues, indexedProperty, machine.getCode());
		}
		return fieldValues;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final Object value) {
		final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames) {
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}
}
