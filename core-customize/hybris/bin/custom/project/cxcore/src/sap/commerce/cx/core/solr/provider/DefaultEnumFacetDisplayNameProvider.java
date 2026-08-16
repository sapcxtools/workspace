package sap.commerce.cx.core.solr.provider;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

/**
 * Resolves localized display names for facet values based on a configured
 * SAP Commerce enumeration type.
 *
 * <p>The provider uses the configured enumeration code to resolve the
 * corresponding {@link HybrisEnumValue} and returns its localized name
 * for the current search language. If no localized name can be resolved,
 * the raw facet value is returned as a fallback.</p>
 */
public class DefaultEnumFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider {
	private final EnumerationService enumerationService;
	private final I18NService i18nService;
	private final CommonI18NService commonI18NService;
	private final String enumerationCode;

	/**
	 * Creates a provider for the specified enumeration type.
	 *
	 * @param enumerationService service used to resolve enumeration values and labels
	 * @param i18nService        service used to determine the fallback locale
	 * @param commonI18NService  service used to resolve locales from search languages
	 * @param enumerationCode    code of the SAP Commerce enumeration type
	 */
	public DefaultEnumFacetDisplayNameProvider(final EnumerationService enumerationService,
			final I18NService i18nService,
			final CommonI18NService commonI18NService,
			String enumerationCode) {
		this.enumerationService = enumerationService;
		this.i18nService = i18nService;
		this.commonI18NService = commonI18NService;
		this.enumerationCode = enumerationCode;
	}

	/**
	 * Returns the localized display name for the given facet value.
	 *
	 * @param query      the current search query
	 * @param property   the indexed property associated with the facet
	 * @param facetValue the raw facet value
	 * @return the localized enumeration label, or the raw facet value if no label is found
	 */
	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue) {
		if (facetValue == null) {
			return "";
		}
		final HybrisEnumValue enumValue = enumerationService.getEnumerationValue(enumerationCode, facetValue);
		final String enumName = enumerationService.getEnumerationName(enumValue, getLocale(query));

		return StringUtils.isNotEmpty(enumName) ? enumName : facetValue;
	}

	/**
	 * Resolves the locale to be used for label localization.
	 *
	 * <p>If the search query contains a language, the corresponding locale
	 * is returned. Otherwise, the current session locale is used.</p>
	 *
	 * @param query the current search query
	 * @return the locale used for localization
	 */
	private Locale getLocale(final SearchQuery query) {
		if (query != null && StringUtils.isNotEmpty(query.getLanguage())) {
			return commonI18NService.getLocaleForLanguage(commonI18NService.getLanguage(query.getLanguage()));
		}
		return i18nService.getCurrentLocale();
	}
}
