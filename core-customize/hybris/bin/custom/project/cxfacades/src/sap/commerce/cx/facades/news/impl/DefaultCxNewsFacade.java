package sap.commerce.cx.facades.news.impl;

import java.util.List;
import java.util.Objects;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxNewsModel;
import sap.commerce.cx.core.news.service.CxNewsService;
import sap.commerce.cx.facades.news.CxNewsFacade;
import sap.commerce.cx.facades.news.data.CxNewsData;

/**
 * Default implementation of {@link CxNewsFacade}.
 *
 * <p>Delegates retrieval to {@link CxNewsService} and converts each
 * {@link CxNewsModel} to {@link CxNewsData} using the injected converter.
 */
public class DefaultCxNewsFacade implements CxNewsFacade {
	private final CxNewsService cxNewsService;
	private final Converter<CxNewsModel, CxNewsData> cxNewsConverter;

	public DefaultCxNewsFacade(
			final CxNewsService cxNewsService,
			final Converter<CxNewsModel, CxNewsData> cxNewsConverter) {
		this.cxNewsService = Objects.requireNonNull(cxNewsService);
		this.cxNewsConverter = Objects.requireNonNull(cxNewsConverter);
	}

	@Override
	public List<CxNewsData> getLatestActiveNews() {
		return Converters.convertAll(cxNewsService.getLatestActiveNews(), cxNewsConverter);
	}

	/** {@inheritDoc} */
	@Override
	public List<CxNewsData> getLatestActiveNews(final int count) {
		return Converters.convertAll(cxNewsService.getLatestActiveNews(count), cxNewsConverter);
	}

	@Override
	public List<CxNewsData> getAllActiveNews() {
		return Converters.convertAll(
				cxNewsService.getAllActiveNews(),
				cxNewsConverter);
	}
}
