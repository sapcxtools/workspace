package sap.commerce.cx.facades.cart;

import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;

import sap.commerce.cx.facades.data.cart.CxSaveCartParameterData;

public interface CxSaveCartFacade {
	/**
	 * Method saves a cart
	 *
	 * @param parameters
	 *           {@link CxSaveCartParameterData} parameter object that holds the Id of the cart to be saved along
	 *           with some additional details such as a name and a description for this cart. It also holds the
	 *           information if the Active cart should be cloned for save
	 * @return {@link CommerceSaveCartResultData}
	 * @throws CommerceSaveCartException
	 *            if cart cannot be saved
	 */
	CommerceSaveCartResultData saveCart(CxSaveCartParameterData parameters) throws CommerceSaveCartException;
}
