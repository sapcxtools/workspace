package sap.commerce.cx.occ.controllers;

import static de.hybris.platform.commercefacades.order.constants.OrderOccControllerRequestFromConstants.SAVE_CART_CONTROLLER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservices.core.requestfrom.RequestFromValueSetter;
import de.hybris.platform.commercewebservices.core.skipfield.SkipSaveCartResultFieldValueSetter;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPSavedCartRequestWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.SaveCartResultWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import sap.commerce.cx.facades.cart.CxSaveCartFacade;
import sap.commerce.cx.facades.data.cart.CxSaveCartParameterData;

/**
 * Overrides the standard save cart endpoint to support the {@code keepActiveCart} parameter.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
public class CxSaveCartController extends BaseController {
	@Resource(name = "cxSaveCartFacade")
	private CxSaveCartFacade cxSaveCartFacade;
	@Resource(name = "skipSaveCartResultFieldValueSetter")
	private SkipSaveCartResultFieldValueSetter skipSaveCartResultFieldValueSetter;
	@Resource(name = "requestFromValueSetter")
	private RequestFromValueSetter requestFromValueSetter;

	@PatchMapping(value = "/{cartId}/savedCart", consumes = APPLICATION_JSON_VALUE)
	@RequestMappingOverride(priorityProperty = "cxocc.CxSaveCartController.doCartSave.priority")
	@ResponseBody
	@Operation(operationId = "doCartSave", summary = "Updates a cart to save it.", description = "Updates a cart to explicitly save it. Adds the name and description of the saved cart if specified. This endpoint is added in the 2211.28 update.")
	@ApiBaseSiteIdAndUserIdParam
	public SaveCartResultWsDTO doCartSave(
			@Parameter(description = "Cart identifier: cart code for logged-in user, cart GUID for anonymous user, or 'current' for the last modified cart.", required = true) @PathVariable final String cartId,
			@RequestBody SAPSavedCartRequestWsDTO savedCart,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CommerceSaveCartException {
		skipSaveCartResultFieldValueSetter.setValue(fields);
		requestFromValueSetter.setRequestFrom(SAVE_CART_CONTROLLER);

		final CommerceSaveCartResultData result = cxSaveCartFacade.saveCart(getCxSaveCartParameterData(cartId, savedCart));

		return getDataMapper().map(result, SaveCartResultWsDTO.class, fields);
	}

	private CxSaveCartParameterData getCxSaveCartParameterData(final String cartId, final SAPSavedCartRequestWsDTO savedCart) {
		final CxSaveCartParameterData parameters = new CxSaveCartParameterData();
		parameters.setCartId(cartId);
		parameters.setName(savedCart.getName());
		parameters.setEnableHooks(true);
		parameters.setKeepActiveCart(savedCart.isKeepActiveCart());
		parameters.setDescription(savedCart.getDescription());
		return parameters;
	}
}
