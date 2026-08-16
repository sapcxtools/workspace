package sap.commerce.cx.facades.cart.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import sap.commerce.cx.core.cart.CxSaveCartService;
import sap.commerce.cx.facades.cart.CxSaveCartFacade;
import sap.commerce.cx.facades.data.cart.CxSaveCartParameterData;

/**
 * CX-specific extension of {@link DefaultSaveCartFacade} that adds support for
 * keeping the active cart when saving. If {@code keepActiveCart} is set, the active
 * cart is cloned before saving so the original remains in the session.
 */
public class DefaultCxSaveCartFacade implements CxSaveCartFacade, SaveCartFacade {

	private final DefaultSaveCartFacade delegate;
	private final CxSaveCartService saveCartService;
	private final CartService cartService;
	private final CommerceCartService commerceCartService;
	private final UserService userService;
	private final Converter<CartModel, CartData> cartConverter;

	public DefaultCxSaveCartFacade(final DefaultSaveCartFacade delegate,
			final CxSaveCartService saveCartService,
			final CartService cartService,
			final CommerceCartService commerceCartService,
			final UserService userService,
			final Converter<CartModel, CartData> cartConverter) {
		this.delegate = delegate;
		this.saveCartService = saveCartService;
		this.cartService = cartService;
		this.commerceCartService = commerceCartService;
		this.userService = userService;
		this.cartConverter = cartConverter;
	}

	@Override
	public CommerceSaveCartResultData saveCart(final CxSaveCartParameterData inputParameters) throws CommerceSaveCartException {
		final CartModel cart = resolveCart(inputParameters.getCartId());

		final CommerceSaveCartResult result = saveCartService.saveCart(
				cart,
				inputParameters.getName(),
				inputParameters.getDescription(),
				inputParameters.isEnableHooks(),
				inputParameters.isKeepActiveCart());

		final CommerceSaveCartResultData resultData = new CommerceSaveCartResultData();
		resultData.setSavedCartData(cartConverter.convert(result.getSavedCart()));
		return resultData;
	}

	private CartModel resolveCart(final String cartId) throws CommerceSaveCartException {
		if (StringUtils.isEmpty(cartId)) {
			return cartService.getSessionCart();
		}
		final CartModel cart = commerceCartService.getCartForCodeAndUser(cartId, userService.getCurrentUser());
		if (cart == null) {
			throw new CommerceSaveCartException("Cannot find a cart for code [" + cartId + "]");
		}
		return cart;
	}

	@Override
	public CommerceSaveCartResultData saveCart(CommerceSaveCartParameterData parameters) throws CommerceSaveCartException {
		return delegate.saveCart(parameters);
	}

	@Override
	public CommerceSaveCartResultData flagForDeletion(String cartId) throws CommerceSaveCartException {
		return delegate.flagForDeletion(cartId);
	}

	@Override
	public CommerceSaveCartResultData getCartForCodeAndCurrentUser(CommerceSaveCartParameterData parameters) throws CommerceSaveCartException {
		return delegate.getCartForCodeAndCurrentUser(parameters);
	}

	@Override
	public CartRestorationData restoreSavedCart(CommerceSaveCartParameterData parameters) throws CommerceSaveCartException {
		return delegate.restoreSavedCart(parameters);
	}

	@Override
	public SearchPageData<CartData> getSavedCartsForCurrentUser(PageableData pageableData, List<OrderStatus> orderStatus) {
		return delegate.getSavedCartsForCurrentUser(pageableData, orderStatus);
	}

	@Override
	public CommerceSaveCartResultData cloneSavedCart(CommerceSaveCartParameterData parameter) throws CommerceSaveCartException {
		return delegate.cloneSavedCart(parameter);
	}

	@Override
	public Integer getSavedCartsCountForCurrentUser() {
		return delegate.getSavedCartsCountForCurrentUser();
	}
}
