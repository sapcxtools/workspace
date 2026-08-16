package sap.commerce.cx.cpi.hook;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;

/**
 * Product-specific pre-persist hook for SAP CPI inbound integration processing.
 * <p>
 * This hook is executed during inbound persistence processing for product items
 * and can be used to apply additional validation, enrichment, or preprocessing
 * logic before the item is persisted.
 * </p>
 */
public class CxSapCpiProductPersistenceHook implements PrePersistHook {
	private static final Logger LOG = LoggerFactory.getLogger(CxSapCpiProductPersistenceHook.class);

	/**
	 * Executes custom pre-persist logic for product items.
	 *
	 * @param item the item currently processed for persistence
	 * @param context the inbound persistence context
	 * @return the item to persist wrapped in an {@link Optional},
	 *         or an empty optional to skip persistence
	 */
	@Override
	public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context) {
		if (!(item instanceof ProductModel product)) {
			return Optional.of(item);
		}

		LOG.warn("DEMO Content");
		return Optional.of(item);
	}
}
