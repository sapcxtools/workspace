package tools.sapcx.commerce.search.facet.fallback;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetValueField;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CxFallbackToDefaultValueFacetSearchListener implements FacetSearchListener, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private ExpressionParser parser;

	@Override
	public void beforeSearch(FacetSearchContext facetSearchContext) throws FacetSearchException {
		Collection<IndexedProperty> indexedProperties = MapUtils.emptyIfNull(facetSearchContext.getIndexedType().getIndexedProperties()).values();
		Map<String, Boolean> propertiesWithFacetValues = ListUtils.emptyIfNull(facetSearchContext.getSearchQuery().getFacetValues()).stream()
				.collect(Collectors.toMap(FacetValueField::getField, f -> CollectionUtils.isNotEmpty(f.getValues())));

		for (IndexedProperty indexedProperty : indexedProperties) {
			if (indexedProperty.isForceDefaultValue()) {
				if (Boolean.TRUE.equals(propertiesWithFacetValues.get(indexedProperty.getName()))) {
					Iterator<FacetValueField> iterator = facetSearchContext.getSearchQuery().getFacetValues().iterator();
					while (iterator.hasNext()) {
						FacetValueField field = iterator.next();
						if (StringUtils.equals(field.getField(), indexedProperty.getName())) {
							iterator.remove();
						}
					}
				}
				addDefaultValue(facetSearchContext, indexedProperty);
			} else if (indexedProperty.isForceDefaultValueIfEmpty()) {
				if (!Boolean.TRUE.equals(propertiesWithFacetValues.get(indexedProperty.getName()))) {
					addDefaultValue(facetSearchContext, indexedProperty);
				}
			}
		}
	}

	@Override
	public void afterSearch(FacetSearchContext facetSearchContext) throws FacetSearchException {
	}

	@Override
	public void afterSearchError(FacetSearchContext facetSearchContext) throws FacetSearchException {
	}

	private void addDefaultValue(FacetSearchContext facetSearchContext, IndexedProperty indexedProperty) {
		Object value = evaluateDefaultValue(facetSearchContext, indexedProperty);
		if (value != null) {
			if (indexedProperty.isDefaultValueVisibleToConsumer()) {
				facetSearchContext.getSearchQuery().addFacetValue(indexedProperty.getName(), value.toString());
			} else {
				facetSearchContext.getSearchQuery().addFacetValue(indexedProperty.getExportId(), value.toString());
			}
		}
	}

	private Object evaluateDefaultValue(FacetSearchContext facetSearchContext, IndexedProperty indexedProperty) {
		String defaultValueExpression = indexedProperty.getDefaultValueExpression();
		Expression parsedExpression = this.parser.parseExpression(defaultValueExpression);
		return parsedExpression.getValue(getEvaluationContext(facetSearchContext, indexedProperty));
	}

	private StandardEvaluationContext getEvaluationContext(FacetSearchContext facetSearchContext, IndexedProperty indexedProperty) {
		StandardEvaluationContext context = new StandardEvaluationContext(indexedProperty);
		context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));
		context.setVariable("searchQuery", facetSearchContext.getSearchQuery());
		context.setVariable("indexedType", facetSearchContext.getIndexedType());
		context.setVariable("indexedProperty", indexedProperty);
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Required
	public void setParser(ExpressionParser parser) {
		this.parser = parser;
	}
}
