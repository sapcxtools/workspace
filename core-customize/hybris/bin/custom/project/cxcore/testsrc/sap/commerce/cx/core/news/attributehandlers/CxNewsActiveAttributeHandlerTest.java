package sap.commerce.cx.core.news.attributehandlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.time.TimeService;

import sap.commerce.cx.core.model.CxNewsModel;

@UnitTest
public class CxNewsActiveAttributeHandlerTest {
	private static final Date NOW = new Date(1_700_000_000_000L);
	private static final Date BEFORE_NOW = new Date(NOW.getTime() - 1_000L);
	private static final Date AFTER_NOW = new Date(NOW.getTime() + 1_000L);

	private CxNewsActiveAttributeHandler handler;
	private TimeService timeService;
	private CxNewsModel news;

	@Before
	public void setUp() {
		timeService = mock(TimeService.class);
		when(timeService.getCurrentTime()).thenReturn(NOW);

		handler = new CxNewsActiveAttributeHandler(timeService);

		news = new CxNewsModel();
	}

	@Test
	public void shouldBeActiveWhenValidFromAndValidToAreNull() {
		news.setValidFrom(null);
		news.setValidTo(null);

		final Boolean result = handler.get(news);

		assertTrue(result);
	}

	@Test
	public void shouldBeActiveWhenNowIsAfterValidFromAndBeforeValidTo() {
		news.setValidFrom(BEFORE_NOW);
		news.setValidTo(AFTER_NOW);

		final Boolean result = handler.get(news);

		assertTrue(result);
	}

	@Test
	public void shouldBeActiveWhenNowIsAfterValidFromAndValidToIsNull() {
		news.setValidFrom(BEFORE_NOW);
		news.setValidTo(null);

		final Boolean result = handler.get(news);

		assertTrue(result);
	}

	@Test
	public void shouldBeActiveWhenValidFromIsNullAndNowIsBeforeValidTo() {
		news.setValidFrom(null);
		news.setValidTo(AFTER_NOW);

		final Boolean result = handler.get(news);

		assertTrue(result);
	}

	@Test
	public void shouldNotBeActiveWhenNowEqualsValidFrom() {
		news.setValidFrom(NOW);
		news.setValidTo(AFTER_NOW);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test
	public void shouldNotBeActiveWhenNowEqualsValidTo() {
		news.setValidFrom(BEFORE_NOW);
		news.setValidTo(NOW);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test
	public void shouldNotBeActiveWhenValidFromIsInFuture() {
		news.setValidFrom(AFTER_NOW);
		news.setValidTo(null);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test
	public void shouldNotBeActiveWhenValidToIsInPast() {
		news.setValidFrom(null);
		news.setValidTo(BEFORE_NOW);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test
	public void shouldNotBeActiveWhenNowEqualsValidFromAndValidToIsNull() {
		news.setValidFrom(NOW);
		news.setValidTo(null);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test
	public void shouldNotBeActiveWhenValidFromIsNullAndNowEqualsValidTo() {
		news.setValidFrom(null);
		news.setValidTo(NOW);

		final Boolean result = handler.get(news);

		assertFalse(result);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowExceptionWhenTryingToSetActive() {
		handler.set(news, Boolean.TRUE);
	}
}
