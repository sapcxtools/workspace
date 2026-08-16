package sap.commerce.cx.cpi.hook;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sap.commerce.cx.cpi.hook.CxPersistenceHookExecutorImpl.CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL;
import static sap.commerce.cx.cpi.hook.CxPersistenceHookExecutorImpl.CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX;
import static sap.commerce.cx.cpi.hook.CxPersistenceHookExecutorImpl.CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL;
import static sap.commerce.cx.cpi.hook.CxPersistenceHookExecutorImpl.CX_INTEGRATION_PREPERSISTHOOKS_PREFIX;

import java.util.Optional;

import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookExecutor;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookExecutionException;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxPersistenceHookExecutorImplTest {
	private static final String ITEM_TYPE = "Product";

	@Mock
	private PersistenceHookExecutor delegate;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private PersistenceContext context;

	@Mock
	private ItemModel item;

	@Mock
	private PrePersistHook prePersistHookA;

	@Mock
	private PrePersistHook prePersistHookB;

	@Mock
	private PostPersistHook postPersistHookA;

	@Mock
	private PostPersistHook postPersistHookB;

	private CxPersistenceHookExecutorImpl systemUnderTest;

	@BeforeEach
	void setUp() {
		systemUnderTest = new CxPersistenceHookExecutorImpl(delegate, configurationService);
		systemUnderTest.setApplicationContext(applicationContext);
	}

	// =====================================================================
	// runPrePersistHook – delegate
	// =====================================================================

	@Test
	void runPrePersistHook_alwaysDelegatesToDelegate() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");

		systemUnderTest.runPrePersistHook(item, context);

		verify(delegate).runPrePersistHook(item, context);
	}

	@Test
	void runPrePersistHook_returnsItemFromDelegateWhenNoAdditionalHooksConfigured() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertTrue(result.isPresent());
		assertSame(item, result.get());
	}

	@Test
	void runPrePersistHook_returnsEmptyAndSkipsAdditionalHooksWhenDelegateReturnsEmpty() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.empty());

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertFalse(result.isPresent());
		verify(applicationContext, never()).getBean(anyString(), eq(PrePersistHook.class));
	}

	// =====================================================================
	// runPrePersistHook – global hooks
	// =====================================================================

	@Test
	void runPrePersistHook_executesGlobalHooksInOrder() {
		final ItemModel updatedItem = mock(ItemModel.class);

		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("hookA, hookB");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(applicationContext.getBean("hookB", PrePersistHook.class)).thenReturn(prePersistHookB);
		when(prePersistHookA.execute(item, context)).thenReturn(Optional.of(item));
		when(prePersistHookB.execute(item, context)).thenReturn(Optional.of(updatedItem));

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertTrue(result.isPresent());
		assertSame(updatedItem, result.get());
		final InOrder inOrder = inOrder(prePersistHookA, prePersistHookB);
		inOrder.verify(prePersistHookA).execute(item, context);
		inOrder.verify(prePersistHookB).execute(item, context);
	}

	@Test
	void runPrePersistHook_stopsChainAndReturnsEmptyWhenGlobalHookReturnsEmpty() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("hookA");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(prePersistHookA.execute(item, context)).thenReturn(Optional.empty());

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertFalse(result.isPresent());
		// item-type-specific config must not even be queried since the optional is already empty
		verify(applicationContext, never()).getBean(anyString(), eq(PostPersistHook.class));
	}

	@Test
	void runPrePersistHook_ignoresMissingBeanForGlobalHook() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("missingHook");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");
		when(applicationContext.getBean("missingHook", PrePersistHook.class))
				.thenThrow(new NoSuchBeanDefinitionException("missingHook"));

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertTrue(result.isPresent());
		assertSame(item, result.get());
	}

	@Test
	void runPrePersistHook_ignoresWrongTypeBeanForGlobalHook() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("wrongTypeHook");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");
		when(applicationContext.getBean("wrongTypeHook", PrePersistHook.class))
				.thenThrow(new BeanNotOfRequiredTypeException("wrongTypeHook", PrePersistHook.class, Object.class));

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertTrue(result.isPresent());
		assertSame(item, result.get());
	}

	@Test
	void runPrePersistHook_wrapsRuntimeExceptionFromGlobalHook() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("hookA");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(prePersistHookA.execute(item, context)).thenThrow(new RuntimeException("global hook failed"));

		assertThrows(PersistenceHookExecutionException.class, () -> systemUnderTest.runPrePersistHook(item, context));
	}

	// =====================================================================
	// runPrePersistHook – item-type-specific hooks
	// =====================================================================

	@Test
	void runPrePersistHook_executesItemTypeSpecificHook() {
		final ItemModel updatedItem = mock(ItemModel.class);

		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("hookA");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(prePersistHookA.execute(item, context)).thenReturn(Optional.of(updatedItem));

		final Optional<ItemModel> result = systemUnderTest.runPrePersistHook(item, context);

		assertTrue(result.isPresent());
		assertSame(updatedItem, result.get());
		verify(prePersistHookA).execute(item, context);
	}

	@Test
	void runPrePersistHook_executesGlobalHooksBeforeItemTypeSpecificHooks() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("hookA, hookB");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(applicationContext.getBean("hookB", PrePersistHook.class)).thenReturn(prePersistHookB);
		when(prePersistHookA.execute(item, context)).thenReturn(Optional.of(item));
		when(prePersistHookB.execute(item, context)).thenReturn(Optional.of(item));

		systemUnderTest.runPrePersistHook(item, context);

		final InOrder inOrder = inOrder(prePersistHookA, prePersistHookB);
		inOrder.verify(prePersistHookA).execute(item, context);
		inOrder.verify(prePersistHookB).execute(item, context);
	}

	@Test
	void runPrePersistHook_wrapsRuntimeExceptionFromItemTypeSpecificHook() {
		when(delegate.runPrePersistHook(item, context)).thenReturn(Optional.of(item));
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_PREPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("hookA");
		when(applicationContext.getBean("hookA", PrePersistHook.class)).thenReturn(prePersistHookA);
		when(prePersistHookA.execute(item, context)).thenThrow(new RuntimeException("item-type hook failed"));

		assertThrows(PersistenceHookExecutionException.class, () -> systemUnderTest.runPrePersistHook(item, context));
	}

	// =====================================================================
	// runPostPersistHook – delegate
	// =====================================================================

	@Test
	void runPostPersistHook_alwaysDelegatesToDelegate() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");

		systemUnderTest.runPostPersistHook(item, context);

		verify(delegate).runPostPersistHook(item, context);
	}

	// =====================================================================
	// runPostPersistHook – global hooks
	// =====================================================================

	@Test
	void runPostPersistHook_executesGlobalPostHooksInOrderAfterDelegate() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("postHookA");
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("postHookB");
		when(applicationContext.getBean("postHookA", PostPersistHook.class)).thenReturn(postPersistHookA);
		when(applicationContext.getBean("postHookB", PostPersistHook.class)).thenReturn(postPersistHookB);

		systemUnderTest.runPostPersistHook(item, context);

		final InOrder inOrder = inOrder(delegate, postPersistHookA, postPersistHookB);
		inOrder.verify(delegate).runPostPersistHook(item, context);
		inOrder.verify(postPersistHookA).execute(item, context);
		inOrder.verify(postPersistHookB).execute(item, context);
	}

	@Test
	void runPostPersistHook_ignoresMissingBeanForGlobalPostHook() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("missingHook");
		when(applicationContext.getBean("missingHook", PostPersistHook.class))
				.thenThrow(new NoSuchBeanDefinitionException("missingHook"));
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");

		assertDoesNotThrow(() -> systemUnderTest.runPostPersistHook(item, context));
	}

	@Test
	void runPostPersistHook_ignoresWrongTypeBeanForGlobalPostHook() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("wrongTypeHook");
		when(applicationContext.getBean("wrongTypeHook", PostPersistHook.class))
				.thenThrow(new BeanNotOfRequiredTypeException("wrongTypeHook", PostPersistHook.class, Object.class));
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("");

		assertDoesNotThrow(() -> systemUnderTest.runPostPersistHook(item, context));
	}

	@Test
	void runPostPersistHook_wrapsRuntimeExceptionFromGlobalPostHook() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("postHookA");
		when(applicationContext.getBean("postHookA", PostPersistHook.class)).thenReturn(postPersistHookA);
		doThrow(new RuntimeException("global post hook failed")).when(postPersistHookA).execute(item, context);

		assertThrows(PersistenceHookExecutionException.class, () -> systemUnderTest.runPostPersistHook(item, context));
	}

	// =====================================================================
	// runPostPersistHook – item-type-specific hooks
	// =====================================================================

	@Test
	void runPostPersistHook_executesItemTypeSpecificPostHook() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("postHookA");
		when(applicationContext.getBean("postHookA", PostPersistHook.class)).thenReturn(postPersistHookA);

		systemUnderTest.runPostPersistHook(item, context);

		verify(postPersistHookA).execute(item, context);
	}

	@Test
	void runPostPersistHook_executesGlobalPostHooksBeforeItemTypeSpecificPostHooks() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("postHookA");
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("postHookB");
		when(applicationContext.getBean("postHookA", PostPersistHook.class)).thenReturn(postPersistHookA);
		when(applicationContext.getBean("postHookB", PostPersistHook.class)).thenReturn(postPersistHookB);

		systemUnderTest.runPostPersistHook(item, context);

		final InOrder inOrder = inOrder(postPersistHookA, postPersistHookB);
		inOrder.verify(postPersistHookA).execute(item, context);
		inOrder.verify(postPersistHookB).execute(item, context);
	}

	@Test
	void runPostPersistHook_wrapsRuntimeExceptionFromItemTypeSpecificPostHook() {
		when(item.getItemtype()).thenReturn(ITEM_TYPE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_GLOBAL), eq(""))).thenReturn("");
		when(configuration.getString(eq(CX_INTEGRATION_POSTPERSISTHOOKS_PREFIX + ITEM_TYPE), eq(""))).thenReturn("postHookA");
		when(applicationContext.getBean("postHookA", PostPersistHook.class)).thenReturn(postPersistHookA);
		doThrow(new RuntimeException("item-type post hook failed")).when(postPersistHookA).execute(item, context);

		assertThrows(PersistenceHookExecutionException.class, () -> systemUnderTest.runPostPersistHook(item, context));
	}
}
