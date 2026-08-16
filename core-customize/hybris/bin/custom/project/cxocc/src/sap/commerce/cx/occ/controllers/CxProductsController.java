/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package sap.commerce.cx.occ.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.google.common.collect.Lists;

import de.hybris.platform.acceleratorfacades.order.data.PriceRangeData;
import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.*;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;

/**
 * Web Services Controller to expose the functionality of the {@link ProductFacade} and SearchFacade.
 */

@Controller
@Tag(name = "Products")
@RequestMapping(value = "/{baseSiteId}/products")
public class CxProductsController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(CxProductsController.class);

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@GetMapping("/{productCode}")
	@RequestMappingOverride
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.ProductCacheKeyGenerator).generateKey(true,true,#productCode,#fields)")
	@ResponseBody
	@Operation(operationId = "getProduct", summary = "Retrieves product details.", description = "Retrieves the details of a single product using the product identifier.")
	@ApiBaseSiteIdParam
	public ProductWsDTO getProduct(
			@Parameter(description = "Product identifier.", required = true) @PathVariable final String productCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		Collection<ProductOption> options = extractProductOptions(fields);
		final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
				adjustProductOptions(options));
		return getDataMapper().map(product, ProductWsDTO.class, fields);
	}

	protected Collection<ProductOption> extractProductOptions(final String fields) {
		final ProductData tempProductData = new ProductData();
		tempProductData.setImages(Lists.newArrayList(new ImageData()));
		tempProductData.setReviews(Lists.newArrayList(new ReviewData()));
		tempProductData.setNumberOfReviews(Integer.valueOf(0));
		tempProductData.setPotentialPromotions(Lists.newArrayList(new PromotionData()));
		tempProductData.setPrice(new PriceData());
		tempProductData.setPurchasable(Boolean.FALSE);
		tempProductData.setPriceRange(new PriceRangeData());
		tempProductData.setStock(new StockData());
		tempProductData.setSapUnit(new SAPUnitData());

		final ProductWsDTO productWsDTO = getDataMapper().map(tempProductData, ProductWsDTO.class, fields);
		final boolean skipImages = CollectionUtils.isEmpty(productWsDTO.getImages());
		final EnumSet<ProductOption> options = EnumSet.allOf(ProductOption.class);
		if (skipImages) {
			options.remove(ProductOption.IMAGES);
			options.remove(ProductOption.GALLERY);
		}
		final boolean skipReviews = CollectionUtils.isEmpty(productWsDTO.getReviews()) && productWsDTO.getNumberOfReviews() == null;
		if (skipReviews) {
			options.remove(ProductOption.REVIEW);
		}
		final boolean skipPromotions = CollectionUtils.isEmpty(productWsDTO.getPotentialPromotions());
		if (skipPromotions) {
			options.remove(ProductOption.PROMOTIONS);
		}
		final boolean skipPrice = productWsDTO.getPrice() == null && productWsDTO.getPurchasable() == null
				&& productWsDTO.getSapUnit() == null;
		if (skipPrice) {
			options.remove(ProductOption.PRICE);
		}
		final boolean skipPriceRange = productWsDTO.getPriceRange() == null;
		if (skipPriceRange) {
			options.remove(ProductOption.PRICE_RANGE);
		}
		final boolean skipStock = productWsDTO.getStock() == null;
		if (skipStock) {
			options.remove(ProductOption.STOCK);
		}
		return options;
	}

	private Collection<ProductOption> adjustProductOptions(final Collection<ProductOption> options) {
		ArrayList<ProductOption> adjustedOptions = new ArrayList<>();
		adjustedOptions.add(ProductOption.BASIC);
		options.forEach(option -> {
			if (option != ProductOption.BASIC) {
				adjustedOptions.add(option);
			}
		});
		return adjustedOptions;
	}
}
