package sap.commerce.cx.core.cart.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import sap.commerce.cx.core.cart.CxSaveCartService;

public class DefaultCxCommerceSaveCartService implements CxSaveCartService, CommerceSaveCartService {
	private final CartService cartService;
	private final KeyGenerator keyGenerator;
	private final CommerceSaveCartService delegate;

	public DefaultCxCommerceSaveCartService(CartService cartService, CommerceSaveCartService delegate, KeyGenerator keyGenerator) {
		this.cartService = cartService;
		this.delegate = delegate;
		this.keyGenerator = keyGenerator;
	}

	@Override
	public CommerceSaveCartResult saveCart(CartModel cart, final String name, final String description,
			final boolean enableHooks, final boolean keepActiveCart)
			throws CommerceSaveCartException {

		if (keepActiveCart) {
			cart = cloneCart(cart);
		}

		final CommerceSaveCartParameter parameter = new CommerceSaveCartParameter();
		parameter.setCart(cart);
		parameter.setEnableHooks(enableHooks);
		parameter.setName(generateSaveCartName(cart, name));
		parameter.setDescription(generateSaveCartDescription(cart, description));

		return delegate.saveCart(parameter);
	}

	protected CartModel cloneCart(final CartModel inputCart) throws CommerceSaveCartException {
		try {
			return cartService.clone(null, null, inputCart, keyGenerator.generate().toString());
		} catch (final Exception e) {
			throw new CommerceSaveCartException("Could not clone cart", e);
		}
	}

	protected String generateSaveCartName(final CartModel cart, final String requestedName) {
		return StringUtils.isNotEmpty(requestedName) ? requestedName : cart.getName();
	}

	protected String generateSaveCartDescription(final CartModel cart, final String requestedDescription) {
		return StringUtils.isNotEmpty(requestedDescription) ? requestedDescription : cart.getDescription();
	}

	@Override
	public CommerceSaveCartResult saveCart(CommerceSaveCartParameter parameters) throws CommerceSaveCartException {
		return delegate.saveCart(parameters);
	}

	@Override
	public CommerceSaveCartResult flagForDeletion(CommerceSaveCartParameter parameters) throws CommerceSaveCartException {
		return delegate.flagForDeletion(parameters);
	}

	@Override
	public CommerceCartRestoration restoreSavedCart(CommerceSaveCartParameter parameters) throws CommerceSaveCartException {
		return delegate.restoreSavedCart(parameters);
	}

	@Override
	public SearchPageData<CartModel> getSavedCartsForSiteAndUser(PageableData pageableData, BaseSiteModel baseSite, UserModel user, List<OrderStatus> orderStatus) {
		return delegate.getSavedCartsForSiteAndUser(pageableData, baseSite, user, orderStatus);
	}

	@Override
	public CommerceSaveCartResult cloneSavedCart(CommerceSaveCartParameter parameter) throws CommerceSaveCartException {
		return delegate.cloneSavedCart(parameter);
	}

	@Override
	public Integer getSavedCartsCountForSiteAndUser(BaseSiteModel baseSite, UserModel user) {
		return delegate.getSavedCartsCountForSiteAndUser(baseSite, user);
	}
}
