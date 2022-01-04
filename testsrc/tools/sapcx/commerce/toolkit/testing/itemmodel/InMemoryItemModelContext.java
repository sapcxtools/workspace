package tools.sapcx.commerce.toolkit.testing.itemmodel;

import static tools.sapcx.commerce.toolkit.testing.itemmodel.InMemoryModelFactory.attributeFor;
import static tools.sapcx.commerce.toolkit.testing.itemmodel.InMemoryModelFactory.localizedAttributeFor;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelInternalContext;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

class InMemoryItemModelContext implements ItemModelInternalContext {
	private final PK pk;
	private final String typeCode;
	private Map<String, ItemModelAttribute> attributes = new HashMap<>();

	private InMemoryItemModelContext(String typeCode, PK pk) {
		this.pk = pk;
		this.typeCode = typeCode;
	}

	static InMemoryItemModelContext contextWithAttributes(Class<? extends ItemModel> itemType, PK pk) {
		String typeCode = itemType.getSimpleName().replaceAll("Model$", "");
		return new InMemoryItemModelContext(typeCode, pk);
	}

	InMemoryItemModelContext copy(PK pk) {
		InMemoryItemModelContext clone = new InMemoryItemModelContext(typeCode, pk);
		clone.attributes = attributes.values().stream()
				.collect(Collectors.toMap(ItemModelAttribute::getKey, ItemModelAttribute::clone));
		return clone;
	}

	void updateItemAfterCopy(AbstractItemModel newItem) {
		attributes.values().stream()
				.filter(InMemoryItemAwareAttribute.class::isInstance)
				.map(InMemoryItemAwareAttribute.class::cast)
				.forEach(attribute -> attribute.updateItem(newItem));
	}

	public void throwExceptionForAttribute(String key, Throwable throwable) {
		attributes.put(key, new ExceptionThrowingItemModelAttribute(key, throwable));
	}

	@Override
	public String getItemType() {
		return typeCode;
	}

	@Override
	public String getTenantId() {
		return "junit";
	}

	@Override
	public <T> T getValue(String s, T t) {
		return attributes.containsKey(s) ? (T) attributes.get(s).getValue() : t;
	}

	@Override
	public <T> void setPropertyValue(String attribute, T value) {
		this.setValue(attribute, value);

	}

	@Override
	public <T> T getPropertyValue(String attribute) {
		return getValue(attribute, null);
	}

	@Override
	public <T> T setValue(String s, T t) {
		if (attributes.containsKey(s)) {
			attributes.get(s).setValue(t);
		} else {
			attributes.put(s, attributeFor(s, t));
		}
		return t;
	}

	@Override
	public <T> T getLocalizedValue(String s, Locale locale) {
		return attributes.containsKey(s) ? (T) attributes.get(s).getValue(locale) : null;
	}

	@Override
	public <T> T getLocalizedRelationValue(String s, Locale locale) {
		return attributes.containsKey(s) ? (T) attributes.get(s).getValue(locale) : null;
	}

	@Override
	public <T> void setLocalizedValue(String s, Locale locale, T t) {
		if (attributes.containsKey(s)) {
			attributes.get(s).setValue(locale, t);
		} else if (t == null) {
			attributes.put(s, localizedAttributeFor(s, Object.class).withValue(locale, t).build());
		} else {
			attributes.put(s, localizedAttributeFor(s, (Class<T>) t.getClass()).withValue(locale, t).build());
		}
	}

	@Override
	public PK getPK() {
		return pk;
	}

	@Override
	public <T> T getDynamicValue(AbstractItemModel abstractItemModel, String s) {
		return attributes.containsKey(s) ? (T) attributes.get(s).getValue() : null;
	}

	@Override
	public <T> void setDynamicValue(AbstractItemModel abstractItemModel, String s, T t) {
		if (attributes.containsKey(s)) {
			attributes.get(s).setValue(t);
		} else {
			attributes.put(s, new InMemoryDynamicItemModelAttribute<>(s, abstractItemModel, t));
		}
	}

	/**
	 * If more complex logic is required within the testing, a handler for dynamic attributes can be registered.
	 */
	public <T extends ItemModel, VALUE> void setDynamicHandler(T item, String key, DynamicAttributeHandler<VALUE, T> handler) {
		this.attributes.put(key, new InMemoryDynamicItemModelAttribute(key, item, handler));
	}

	@Override
	public long getPersistenceVersion() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean exists() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isNew() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isRemoved() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isUpToDate() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isDirty() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isDirty(String s) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isDirty(String s, Locale locale) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isLoaded(String s) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isLoaded(String s, Locale locale) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public boolean isDynamicAttribute(String attributeName) {
		return (attributes.containsKey(attributeName) && attributes.get(attributeName) instanceof InMemoryDynamicItemModelAttribute);
	}

	@Override
	public Object loadOriginalValue(String s) {
		throw new UnsupportedOperationException("loadOriginalValue not supported by InMenoryItemModelContext, yet!");
	}

	@Override
	public Object loadOriginalValue(String s, Locale locale) {
		throw new UnsupportedOperationException("loadOriginalValue not supported by InMenoryItemModelContext, yet!");
	}

	@Override
	public void unloadAttribute(final String s) {
		throw new UnsupportedOperationException("unloadAttribute not supported by InMenoryItemModelContext, yet!");
	}

	@Override
	public <T> T getOriginalValue(String s) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public <T> T getOriginalValue(String s, Locale locale) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public Map<Locale, Set<String>> getDirtyLocalizedAttributes() {
		throw new UnsupportedOperationException("getDirtyLocalizedAttributes not supported by InMenoryItemModelContext, yet!");
	}

	@Override
	public Set<String> getDirtyAttributes() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public Object getSource() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public <T> T getLocalizedDynamicValue(AbstractItemModel abstractItemModel, String s, Locale locale) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public <T> void setLocalizedDynamicValue(AbstractItemModel abstractItemModel, String s, Locale locale, T t) {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public Object writeReplace(Object o) throws ObjectStreamException {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public PK getNewPK() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public PK generateNewPK() {
		throw new UnsupportedOperationException("This method is not supported with testing, yet");
	}

	@Override
	public int hashCode(AbstractItemModel abstractItemModel) {
		return hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		InMemoryItemModelContext that = (InMemoryItemModelContext) o;
		return Objects.equals(typeCode, that.typeCode) && Objects.equals(attributes, that.attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeCode, attributes);
	}
}
