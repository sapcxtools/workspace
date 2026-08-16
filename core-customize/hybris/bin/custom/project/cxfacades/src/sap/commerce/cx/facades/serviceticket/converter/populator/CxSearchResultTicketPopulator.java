package sap.commerce.cx.facades.serviceticket.converter.populator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import sap.commerce.cx.core.enums.CxServiceTicketStatus;
import sap.commerce.cx.core.enums.CxServiceTicketType;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;

/**
 * Converter implementation for {@link SearchResultValueData} as source and {@link CxServiceTicketData} as target
 * type.
 */
public class CxSearchResultTicketPopulator implements Populator<SearchResultValueData, CxServiceTicketData> {
	private static final Logger LOG = Logger.getLogger(CxSearchResultTicketPopulator.class);
	public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private final CustomerFacade customerFacade;
	private final ProductFacade productFacade;
	private final EnumerationService enumerationService;
	private final CommonI18NService commonI18NService;

	public CxSearchResultTicketPopulator(final CustomerFacade customerFacade, final ProductFacade productFacade,
			final EnumerationService enumerationService, final CommonI18NService commonI18NService) {
		this.customerFacade = customerFacade;
		this.productFacade = productFacade;
		this.enumerationService = enumerationService;
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Populates ticket data from a generic search result map.
	 *
	 * @param source search result source
	 * @param target ticket data target
	 */
	@Override
	public void populate(final SearchResultValueData source, final CxServiceTicketData target) {
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setCode(getValue(source, "code"));
		target.setCreationTime(getDate(getValue(source, "creationtime")));
		target.setType(getValueLocalizedType(source));
		target.setTypeCode(getValue(source, "type"));
		target.setStatus(getValueLocalizedStatus(source));
		target.setTitle(getValue(source, "title"));
		target.setDescription(getValue(source, "description"));
		target.setMachines(getMachineComponentList(getValue(source, "machines")));
		target.setCustomer(getCustomerData(getValue(source, "customer")));
		target.setAllMachines(getValue(source, "allMachines"));
		target.setIsNew(getValue(source, "isNew"));
	}

	/**
	 * Resolves and localizes the ticket type from the search result.
	 */
	private String getValueLocalizedType(final SearchResultValueData source) {
		if (source.getValues() == null) {
			return null;
		}
		final String value = (String) source.getValues().get("type");
		final HybrisEnumValue typeEnumValue = enumerationService.getEnumerationValue(CxServiceTicketType.class, value);
		final Locale locale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());

		return enumerationService.getEnumerationName(typeEnumValue, locale);
	}

	/**
	 * Resolves and localizes the ticket status from the search result.
	 */
	private String getValueLocalizedStatus(final SearchResultValueData source) {
		if (source.getValues() == null) {
			return null;
		}
		final String value = (String) source.getValues().get("status");
		final HybrisEnumValue typeEnumValue = enumerationService.getEnumerationValue(CxServiceTicketStatus.class,
				value);
		final Locale locale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());

		return enumerationService.getEnumerationName(typeEnumValue, locale);
	}

	/**
	 * Parses the Solr timestamp into a {@link Date}.
	 */
	private Date getDate(final String date) {
		if (StringUtils.isEmpty(date)) {
			return null;
		}
		try {
			return dateFormat.parse(date);
		} catch (final ParseException e) {
			LOG.warn(String.format("Value '%s' was not populated due to ParseException.", date));
			return null;
		}
	}

	/**
	 * Resolves machine codes into product data objects.
	 */
	private List<ProductData> getMachineComponentList(final ArrayList<String> machines) {
		if (CollectionUtils.isEmpty(machines)) {
			return new ArrayList<>();
		}
		final List<ProductData> list = new ArrayList<>();
		for (final String machineCode : machines) {
			final ProductData machineData = productFacade.getProductForCodeAndOptions(machineCode,
					List.of(ProductOption.BASIC));
			if (StringUtils.isEmpty(machineData.getCode())) {
				machineData.setCode(machineCode);
			}
			list.add(machineData);
		}
		return list;
	}

	/**
	 * Resolves the customer represented by the given uid.
	 */
	private CustomerData getCustomerData(final String customerId) {
		if (StringUtils.isEmpty(customerId)) {
			return null;
		}
		return customerFacade.getUserForUID(customerId);
	}

	/**
	 * Returns a typed value from the dynamic search result value map.
	 */
	private <T> T getValue(final SearchResultValueData source, final String propertyName) {
		if (source.getValues() == null) {
			return null;
		}

		// DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
		return (T) source.getValues().get(propertyName);
	}
}
