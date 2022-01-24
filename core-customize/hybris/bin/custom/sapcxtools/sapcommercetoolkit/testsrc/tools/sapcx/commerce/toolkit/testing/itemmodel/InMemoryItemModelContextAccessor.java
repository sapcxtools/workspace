package tools.sapcx.commerce.toolkit.testing.itemmodel;

public interface InMemoryItemModelContextAccessor {
	void save();

	void refresh();

	boolean isNew();

	boolean isDirty();
}
