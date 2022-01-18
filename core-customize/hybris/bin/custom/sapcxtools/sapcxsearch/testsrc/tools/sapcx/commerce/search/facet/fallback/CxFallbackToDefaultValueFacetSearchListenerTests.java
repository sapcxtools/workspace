package tools.sapcx.commerce.search.facet.fallback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static tools.sapcx.commerce.testing.dsl.solrsearch.IndexedPropertyBuilder.indexedProperty;
import static tools.sapcx.commerce.testing.dsl.solrsearch.IndexedTypeBuilder.indexedType;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.impl.DefaultFacetSearchContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import tools.sapcx.commerce.testing.testdouble.user.UserServiceFake;

public class CxFallbackToDefaultValueFacetSearchListenerTests {
    private GenericApplicationContext applicationContext;

    private DefaultFacetSearchContext facetSearchContext;
    private SearchQuery searchQuery;

    private CxFallbackToDefaultValueFacetSearchListener listener;

    @Before
    public void setUp() throws Exception {
        IndexedType indexedType = indexedType("product").add(
                indexedProperty("company").type(SolrPropertiesTypes.STRING)
                        .property(item -> item.setForceDefaultValue(true))
                        .property(item -> item.setDefaultValueVisibleToConsumer(true))
                        .property(item -> item.setDefaultValueExpression("'companyId'"))
        ).add(
                indexedProperty("orgUnit").type(SolrPropertiesTypes.STRING)
                        .property(item -> item.setForceDefaultValueIfEmpty(true))
                        .property(item -> item.setDefaultValueVisibleToConsumer(true))
                        .property(item -> item.setDefaultValueExpression("'orgUnitId'"))
        ).add(
                indexedProperty("country").type(SolrPropertiesTypes.STRING)
                        .property(item -> item.setForceDefaultValue(true))
                        .property(item -> item.setDefaultValueVisibleToConsumer(false))
                        .property(item -> item.setDefaultValueExpression("'country'"))
        ).add(
                indexedProperty("user").type(SolrPropertiesTypes.STRING)
                        .property(item -> item.setForceDefaultValue(true))
                        .property(item -> item.setDefaultValueVisibleToConsumer(false))
                        .property(item -> item.setDefaultValueExpression("@userService.getCurrentUser()"))
        ).asData();

        FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
        facetSearchConfig.setName("facetSearchConfig");
        facetSearchConfig.setSearchConfig(getSearchConfig(12, true, true));

        searchQuery = new SearchQuery(facetSearchConfig, indexedType);

        facetSearchContext = new DefaultFacetSearchContext();
        facetSearchContext.setSearchQuery(searchQuery);
        facetSearchContext.setIndexedType(indexedType);

        listener = new CxFallbackToDefaultValueFacetSearchListener();
        listener.setParser(new SpelExpressionParser());

        applicationContext = new GenericApplicationContext();

        UserServiceFake userServiceFake = UserServiceFake.withCustomerId("customer");
        applicationContext.registerBean("userService", userServiceFake.getClass(), userServiceFake.admin, userServiceFake.anonymous, userServiceFake.current);
        applicationContext.refresh();

        listener.setApplicationContext(applicationContext);
    }

    @Test
    public void whenFacetValueIsSet_DefaultFacetValueIsIgnored() throws FacetSearchException {
        searchQuery.addFacetValue("orgUnit", "unit1");

        listener.beforeSearch(facetSearchContext);

        assertThat(searchQuery.getFacetValues()).hasSize(4);
    }

    private SearchConfig getSearchConfig(int pageSize, boolean allFacetValuesInResponse, boolean restrictFieldsInResponse) {
        SearchConfig searchConfig = new SearchConfig();
        searchConfig.setPageSize(pageSize);
        searchConfig.setAllFacetValuesInResponse(allFacetValuesInResponse);
        searchConfig.setRestrictFieldsInResponse(restrictFieldsInResponse);
        return searchConfig;
    }
}