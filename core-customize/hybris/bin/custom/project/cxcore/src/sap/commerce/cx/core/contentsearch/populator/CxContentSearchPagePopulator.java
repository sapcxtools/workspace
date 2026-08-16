package sap.commerce.cx.core.contentsearch.populator;

import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;

public class CxContentSearchPagePopulator<QUERY, STATE, RESULT, ITEM>
		implements Populator<ContentSearchPageData<QUERY, RESULT>, ContentSearchPageData<STATE, ITEM>> {

	private final Converter<QUERY, STATE> searchStateConverter;
	private final Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter;
	private final Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter;
	private final Converter<RESULT, ITEM> searchResultConverter;

	public CxContentSearchPagePopulator(Converter<QUERY, STATE> searchStateConverter,
			Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter,
			Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter,
			Converter<RESULT, ITEM> searchResultConverter) {
		this.searchStateConverter = searchStateConverter;
		this.breadcrumbConverter = breadcrumbConverter;
		this.facetConverter = facetConverter;
		this.searchResultConverter = searchResultConverter;
	}

	/**
	 * Populate the target instance with values from the source instance.
	 *
	 * @param source the source object
	 * @param target the target to fill
	 * @throws ConversionException if an error occurs
	 */
	@Override
	public void populate(ContentSearchPageData<QUERY, RESULT> source, ContentSearchPageData<STATE, ITEM> target) throws ConversionException {
		target.setFreeTextSearch(source.getFreeTextSearch());

		if (source.getBreadcrumbs() != null) {
			target.setBreadcrumbs(Converters.convertAll(source.getBreadcrumbs(), breadcrumbConverter));
		}

		target.setCurrentQuery(searchStateConverter.convert(source.getCurrentQuery()));

		if (source.getFacets() != null) {
			target.setFacets(Converters.convertAll(source.getFacets(), facetConverter));
		}

		target.setPagination(source.getPagination());

		if (source.getResults() != null) {
			target.setResults(Converters.convertAll(source.getResults(), searchResultConverter));
		}

		target.setSorts(source.getSorts());

		target.setKeywordRedirectUrl(source.getKeywordRedirectUrl());
	}
}
