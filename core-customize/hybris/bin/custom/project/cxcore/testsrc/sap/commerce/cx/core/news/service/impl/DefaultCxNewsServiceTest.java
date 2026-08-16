package sap.commerce.cx.core.news.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import sap.commerce.cx.core.model.CxNewsModel;
import sap.commerce.cx.core.news.dao.CxNewsDao;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultCxNewsServiceTest {

	private static final String MAX_ITEMS_PROPERTY = "cx.news.component.maxItems";
	private static final int DEFAULT_MAX_ITEMS = 5;

	@Mock
	private CxNewsDao cxNewsDao;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private DefaultCxNewsService systemUnderTest;

	@BeforeEach
	void setUp() {
		lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		systemUnderTest = new DefaultCxNewsService(cxNewsDao, configurationService);
	}

	@Test
	void shouldReadMaxItemsFromConfigAndReturnActiveNews() {
		when(configuration.getInt(MAX_ITEMS_PROPERTY, DEFAULT_MAX_ITEMS)).thenReturn(3);
		final CxNewsModel active1 = activeNews();
		final CxNewsModel active2 = activeNews();
		final CxNewsModel active3 = activeNews();
		when(cxNewsDao.findNews()).thenReturn(List.of(active1, active2, active3));

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews();

		assertEquals(3, result.size());
	}

	@Test
	void shouldFallBackToDefaultMaxItemsWhenPropertyIsNotSet() {
		when(configuration.getInt(MAX_ITEMS_PROPERTY, DEFAULT_MAX_ITEMS)).thenReturn(DEFAULT_MAX_ITEMS);
		final List<CxNewsModel> news = activeNewsList(DEFAULT_MAX_ITEMS + 2);
		when(cxNewsDao.findNews()).thenReturn(news);

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews();

		assertEquals(DEFAULT_MAX_ITEMS, result.size());
	}

	@Test
	void shouldReturnOnlyActiveNews() {
		final CxNewsModel active = activeNews();
		final CxNewsModel inactive = inactiveNews();
		when(cxNewsDao.findNews()).thenReturn(List.of(active, inactive));

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews(10);

		assertEquals(1, result.size());
		assertEquals(active, result.get(0));
	}

	@Test
	void shouldLimitResultToRequestedCount() {
		final List<CxNewsModel> newsList = activeNewsList(10);
		when(cxNewsDao.findNews()).thenReturn(newsList);

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews(3);

		assertEquals(3, result.size());
	}

	@Test
	void shouldReturnEmptyListWhenNoNewsExist() {
		when(cxNewsDao.findNews()).thenReturn(List.of());

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews(5);

		assertEquals(0, result.size());
	}

	@Test
	void shouldReturnEmptyListWhenAllNewsAreInactive() {
		final CxNewsModel inactive1 = inactiveNews();
		final CxNewsModel inactive2 = inactiveNews();
		when(cxNewsDao.findNews()).thenReturn(List.of(inactive1, inactive2));

		final List<CxNewsModel> result = systemUnderTest.getLatestActiveNews(5);

		assertEquals(0, result.size());
	}

	@Test
	void shouldReplaceZeroWithDefault() {
		assertEquals(DEFAULT_MAX_ITEMS, systemUnderTest.sanitizeCount(0));
	}

	@Test
	void shouldReplaceNegativeCountWithDefault() {
		assertEquals(DEFAULT_MAX_ITEMS, systemUnderTest.sanitizeCount(-1));
	}

	@Test
	void shouldReturnCountUnchangedWhenPositive() {
		assertEquals(7, systemUnderTest.sanitizeCount(7));
	}

	private CxNewsModel activeNews() {
		final CxNewsModel news = mock(CxNewsModel.class);
		lenient().doReturn(Boolean.TRUE).when(news).getActive();
		return news;
	}

	private CxNewsModel inactiveNews() {
		final CxNewsModel news = mock(CxNewsModel.class);
		lenient().doReturn(Boolean.FALSE).when(news).getActive();
		return news;
	}

	private List<CxNewsModel> activeNewsList(final int count) {
		return java.util.stream.IntStream.range(0, count)
				.mapToObj(i -> activeNews())
				.toList();
	}
}
