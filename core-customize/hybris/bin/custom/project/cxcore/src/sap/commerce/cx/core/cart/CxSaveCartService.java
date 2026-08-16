package sap.commerce.cx.core.cart;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.CartModel;

public interface CxSaveCartService {
	CommerceSaveCartResult saveCart(CartModel cart, String name, String description,
			boolean enableHooks, boolean keepActiveCart)
			throws CommerceSaveCartException;
}
