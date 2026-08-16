package sap.commerce.cx.cpi.hook;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookExecutor;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookExecutionException;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import jakarta.annotation.Nonnull;

/**
 * Custom persistence hook executor that delegates to the configure {@link PersistenceHookExecutor}
 * and optionally executes additional global and item-specific pre- and post- persistence hooks.
 *
 * Naming convention for the configuration properties is:
 * - cx.integration.inbound.hooks.prepersist.global=<list of global pre-persist hook beans>
 * - cx.integration.inbound.hooks.prepersist.<itemtype>=<list of itemtype specific pre-persist hook beans>
 * - cx.integration.inbound.hooks.postpersist.global=<list of global post-persist hook beans>
 * - cx.integration.inbound.hooks.postpersist.<itemtype>=<list of itemtype specific post-persist hook beans>
 */
public class CxPersistenceHookExecutorImpl implements PersistenceHookExecutor, ApplicationContextAware {
	private static final Logger LOG = Log.getLogger(CxPersistenceHookExecutorImpl.class);

	static final String CX_INTEGRATION_HOOKS_PREFIX = "cx.integration.inbound.hooks.";
	static final String CX_INTEGRATION_PREPERSISTHOOKS_PREFIX = CX_INTEGRATION_HOOKS_PREFIX + "prepersist.";
	static final String CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL = CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + "global";
	static final String CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX = CX_INTEGRATION_HOOKS_PREFIX + "postpersist.";
	static final String CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL = CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + "global";

	private final PersistenceHookExecutor delegate;
	private final ConfigurationService configurationService;

	private ApplicationContext applicationContext;

	public CxPersistenceHookExecutorImpl(
			final PersistenceHookExecutor delegate,
			final ConfigurationService configurationService) {
		this.delegate = delegate;
		this.configurationService = configurationService;
	}

	/**
	 * Executes the configured pre-persist hook and additional
	 * item-specific hooks if available.
	 *
	 * @param item    the item to be persisted
	 * @param context the persistence context containing hook configuration
	 * @return the item to persist, or an empty optional if persistence should be skipped
	 * @throws {@link PersistenceHookExecutionException} if hook execution fails
	 */
	public Optional<ItemModel> runPrePersistHook(final ItemModel item, final PersistenceContext context) {
		return delegate.runPrePersistHook(item, context)
				.map(mappedItem -> processPrePersistHooksByPrefix(mappedItem, context, CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL))
				.map(mappedItem -> processPrePersistHooksByPrefix(mappedItem, context, CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + item.getItemtype()));
	}

	private ItemModel processPrePersistHooksByPrefix(final ItemModel item, final PersistenceContext context, final String configurationPrefix) {
		final String configValue = configurationService.getConfiguration().getString(configurationPrefix, "");
		final List<String> prePersistHookNames = Arrays.stream(StringUtils.split(configValue, ","))
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.toList();

		ItemModel currentItem = item;
		for (int i = 0; i < prePersistHookNames.size() && currentItem != null; i++) {
			final String prePersistHookName = prePersistHookNames.get(i);
			LOG.debug("Try to execute additional pre-persist hook {} for item type {} with PK {}", prePersistHookName, item.getItemtype(), item.getPk());

			try {
				final Optional<PrePersistHook> hookCandidate = getHookBean(prePersistHookName, PrePersistHook.class);
				if (hookCandidate.isPresent()) {
					currentItem = hookCandidate.get()
							.execute(currentItem, context)
							.orElse(null);
				}
			} catch (final RuntimeException e) {
				throw new PersistenceHookExecutionException(context, prePersistHookName, e);
			}
		}
		return currentItem;
	}

	/**
	 * Executes the default runPostPersistHook method.
	 *
	 * @param item    the item to be persisted
	 * @param context the persistence context containing hook configuration
	 */
	@Override
	public void runPostPersistHook(final ItemModel item, final PersistenceContext context) {
		delegate.runPostPersistHook(item, context);
		processPostPersistHooksByPrefix(item, context, CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL);
		processPostPersistHooksByPrefix(item, context, CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + item.getItemtype());
	}

	private void processPostPersistHooksByPrefix(final ItemModel item, final PersistenceContext context, final String configurationPrefix) {
		final String configValue = configurationService.getConfiguration().getString(configurationPrefix, "");
		final List<String> postPersistHookNames = Arrays.stream(StringUtils.split(configValue, ","))
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.toList();

		for (int i = 0; i < postPersistHookNames.size(); i++) {
			final String postPersistHookName = postPersistHookNames.get(i);
			LOG.debug("Try to execute additional post-persist hook {} for item type {} with PK {}", postPersistHookName, item.getItemtype(), item.getPk());

			try {
				final Optional<PostPersistHook> hookCandidate = getHookBean(postPersistHookName, PostPersistHook.class);
				hookCandidate.ifPresent(hook -> hook.execute(item, context));
			} catch (final RuntimeException e) {
				throw new PersistenceHookExecutionException(context, postPersistHookName, e);
			}
		}
	}

	private <T> Optional<T> getHookBean(final String beanName, final Class<T> hookType) {
		try {
			final T hook = applicationContext.getBean(beanName, hookType);
			return Optional.of(hook);
		} catch (final NoSuchBeanDefinitionException e) {
			LOG.warn("No hook with name {} found. Please check your configuration of {}* properties.", beanName, CX_INTEGRATION_HOOKS_PREFIX, e);
		} catch (final BeanNotOfRequiredTypeException e) {
			LOG.warn("Could not find a hook with name {} that implements the hook {} interface. Please check your configuration of {}* properties.", beanName,
					hookType.getSimpleName(), CX_INTEGRATION_HOOKS_PREFIX, e);
		} catch (final RuntimeException e) {
			LOG.warn("Exception while retrieving hook {}. Please check your configuration of {}* properties.", beanName, CX_INTEGRATION_HOOKS_PREFIX, e);
		}
		return Optional.empty();
	}

	/**
	 * Stores the application context for later bean lookup.
	 *
	 * @param context the application context
	 */
	@Override
	public void setApplicationContext(@Nonnull final ApplicationContext context) {
		applicationContext = context;
	}
}
