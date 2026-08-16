package sap.commerce.cx.facades.populator;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class CxOrderPopulator extends AbstractOrderPopulator<OrderModel, OrderData> {
	private final BaseStoreService baseStoreService;

	public CxOrderPopulator(final BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	private boolean isAbsoluteUrl(final String value) {
		try {
			return new URI(value).isAbsolute();
		} catch (URISyntaxException e) {
			return false;
		}
	}

	@Override
	public void populate(final OrderModel source, final OrderData target) {
		final String trackingId = source.getTrackingId();

		if (StringUtils.isBlank(trackingId)) {
			return;
		}

		if (isAbsoluteUrl(trackingId)) {
			target.setTrackingUrl(trackingId);
			return;
		}

		final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (StringUtils.isNotBlank(baseStoreModel.getTrackingBaseUrl())) {
			target.setTrackingUrl(baseStoreModel.getTrackingBaseUrl() + trackingId);
		}
	}
}
