package tools.sapcx.commerce.search.service;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategyFactory;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
public class DefaultCxIndexerServiceTests {

    private DefaultCxIndexerService defaultCxIndexerService;

    private IndexerStrategyFactory indexerStrategyFactoryMock;
    private IndexerStrategy indexerStrategyMock;
    private FacetSearchConfig facetSearchConfig;
    private IndexConfig indexConfig;

    @Before
    public void setUp() {
        indexerStrategyFactoryMock = mock(IndexerStrategyFactory.class);
        indexerStrategyMock = spy(IndexerStrategy.class);

        defaultCxIndexerService = new DefaultCxIndexerService(indexerStrategyFactoryMock);

        IndexedType indexedTypeA = new IndexedType();
        indexedTypeA.setIdentifier("type-a");

        IndexedType indexedTypeB = new IndexedType();
        indexedTypeB.setIdentifier("type-b");

        IndexedType indexedTypeC = new IndexedType();
        indexedTypeC.setIdentifier("type-c");

        indexConfig = new IndexConfig();
        indexConfig.setIndexedTypes(Map.of("type-a", indexedTypeA, "type-b", indexedTypeB, "type-c", indexedTypeC));

        facetSearchConfig = new FacetSearchConfig();
        facetSearchConfig.setIndexConfig(indexConfig);

    }

    @Test
    public void verifyPerformIndexForIndexedTypes() throws IndexerException {
        when(indexerStrategyFactoryMock.createIndexerStrategy(facetSearchConfig)).thenReturn(indexerStrategyMock);

        defaultCxIndexerService.performIndexForIndexedTypes(facetSearchConfig, IndexOperation.FULL, List.of("type-a", "type-b"));
        verify(indexerStrategyMock, times(2)).execute();
    }

}