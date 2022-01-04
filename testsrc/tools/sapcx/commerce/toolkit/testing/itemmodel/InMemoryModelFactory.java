package tools.sapcx.commerce.toolkit.testing.itemmodel;

import static tools.sapcx.commerce.toolkit.testing.itemmodel.InMemoryItemModelContext.contextWithAttributes;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.apache.commons.lang3.LocaleUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class InMemoryModelFactory {
	private static AtomicLong nextPk = new AtomicLong(System.currentTimeMillis());
	private static Locale defaultLocale = Locale.ENGLISH;

	public static <T extends ItemModel> T createTestableItemModel(Class<T> itemType) {
		return createWithContext(itemType, contextWithAttributes(itemType, PK.fromLong(getNextPk())));
	}

	public static <T extends ItemModel> T createTestableItemModel(Class<T> itemType, int typecode) {
		return createWithContext(itemType, contextWithAttributes(itemType, PK.createFixedCounterPK(typecode, getNextPkForPKCounter())));
	}

	public static <T extends ItemModel> T createTestableItemModel(Class<T> itemType, Map<String, DynamicAttributeHandler> dynamicAttributeHandler) {
		T item = createWithContext(itemType, contextWithAttributes(itemType, PK.fromLong(getNextPk())));
		for (Map.Entry<String, DynamicAttributeHandler> handler : dynamicAttributeHandler.entrySet()) {
			addHandlerForDynamicAttribute(item, handler.getKey(), handler.getValue());
		}
		return item;
	}

	@SuppressWarnings("unchecked")
	public static <T extends ItemModel> T copy(T orig) {
		ItemModelContext itemModelContext = orig.getItemModelContext();
		if (!(itemModelContext instanceof InMemoryItemModelContext)) {
			throw new IllegalArgumentException();
		}

		InMemoryItemModelContext context = (InMemoryItemModelContext) itemModelContext;
		InMemoryItemModelContext clonedContext = context.copy(PK.fromLong(getNextPk()));
		return createWithContext((Class<T>) orig.getClass().getSuperclass(), clonedContext);
	}

	@SuppressWarnings("unchecked")
	private static <T extends ItemModel> T createWithContext(Class<T> itemType, InMemoryItemModelContext context) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(itemType);
		enhancer.setCallback(new ToStringInterceptor<T>());
		T instance = (T) enhancer.create(new Class[] { ItemModelContext.class }, new Object[] { context });
		context.updateItemAfterCopy(instance);
		return instance;
	}

	public static <T extends ItemModel> void throwExceptionForAttribute(T item, String key, Throwable throwable) {
		if (!(item.getItemModelContext() instanceof InMemoryItemModelContext)) {
			throw new IllegalArgumentException();
		} else {
			InMemoryItemModelContext context = (InMemoryItemModelContext) item.getItemModelContext();
			context.throwExceptionForAttribute(key, throwable);
		}
	}

	public static <VALUE, T extends AbstractItemModel> DynamicAttributeHandler<VALUE, T> handlerWithInitialValue(VALUE value) {
		return new InMemoryDynamicAttributeHandler<>(value);
	}

	public static <T extends ItemModel> void addValueForDynamicAttribute(T item, String key, Object value) {
		if (!(item.getItemModelContext() instanceof InMemoryItemModelContext)) {
			throw new IllegalArgumentException();
		} else {
			InMemoryItemModelContext context = (InMemoryItemModelContext) item.getItemModelContext();
			context.setDynamicValue(item, key, value);
		}
	}

	private static <T extends ItemModel, VALUE> void addHandlerForDynamicAttribute(T item, String key, DynamicAttributeHandler<VALUE, T> handler) {
		if (!(item.getItemModelContext() instanceof InMemoryItemModelContext)) {
			throw new IllegalArgumentException();
		} else {
			InMemoryItemModelContext context = (InMemoryItemModelContext) item.getItemModelContext();
			context.setDynamicHandler(item, key, handler);
		}
	}

	public static Locale getDefaultLocale() {
		return defaultLocale;
	}

	public static void setDefaultLocale(String isoCode) {
		defaultLocale = LocaleUtils.toLocale(isoCode);
	}

	public static void setDefaultLocale(Locale locale) {
		defaultLocale = locale;
	}

	public static void resetDefaultLocale() {
		defaultLocale = Locale.ENGLISH;
	}

	/**
	 * Returns getNextPk with the leftmost 17 bits masked, i.e. only the rightmost 47 bits.
	 *
	 * In PK.createPK_Counter() the counter value is left shifted by 16 bits, causing it to become
	 * negative if the 17th bit happens to be a one, which is not allowed.
	 */
	private static long getNextPkForPKCounter() {
		return getNextPk() & 0x7FFFFFFFFFFFL;
	}

	private static long getNextPk() {
		return nextPk.getAndIncrement();
	}

	static ItemModelAttribute attributeFor(String key, Object value) {
		return new InMemoryItemModelAttribute(key, value);
	}

	static <T> LocalizedAttributeBuilder<T> localizedAttributeFor(String key, Class<T> valueType) {
		return new LocalizedAttributeBuilder(key, valueType);
	}

	static class LocalizedAttributeBuilder<T> {
		private InMemoryLocalizedItemModelAttribute attribute;

		public LocalizedAttributeBuilder(String key, Class<T> valueType) {
			this.attribute = new InMemoryLocalizedItemModelAttribute(key);
		}

		public LocalizedAttributeBuilder withValue(Locale locale, T value) {
			this.attribute.setValue(locale, value);
			return this;
		}

		public ItemModelAttribute build() {
			return this.attribute.clone();
		}
	}

	private static class ToStringInterceptor<T extends ItemModel> implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			if (method.getDeclaringClass() != Object.class && method.getName().equals("toString")) {
				return InMemoryModelStringifier.stringifyEnhancedModel((T) obj);
			} else {
				return proxy.invokeSuper(obj, args);
			}
		}
	}
}
