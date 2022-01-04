package tools.sapcx.commerce.toolkit.testing.itemmodel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Function;

import de.hybris.platform.core.model.ItemModel;

class InMemoryModelStringifier {
    /**
     * List of already processed objects to avoid infinite recursion.
     *
     * @see InMemoryItemModelAttribute#equals
     */
    private static final ThreadLocal<Set<Object>> SEEN = new ThreadLocal<>();

    /** recursion depth for indenting */
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    public static String stringifyEnhancedModel(ItemModel item) {
        return executeWithSeen(it -> {
            Set<Object> seen = SEEN.get();
            if (seen.contains(it)) {
                return "(cyclic reference)";
            } else {
                seen.add(it);
                return outerStringify(it);
            }
        }, item);
    }

    private static String outerStringify(ItemModel item) {
        DEPTH.set(DEPTH.get() + 1);
        Class<?> superclass = item.getClass().getSuperclass();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(superclass.getSimpleName());
        builder.append("\n");
        addFields(superclass, builder, item);
        DEPTH.set(DEPTH.get() - 1);
        builder.append("\n");
        indent(builder);
        builder.append("]");
        return builder.toString();
    }

    private static void addFields(Class<?> superclass, StringBuilder builder, ItemModel item) {
        if (superclass == null) {
            return;
        }

        addFields(superclass.getSuperclass(), builder, item);
        boolean first = true;
        for (Field field : superclass.getDeclaredFields()) {
            if (!isFieldInteresting(field)) {
                continue;
            }
            Object value = getFieldValue(item, field);
            if (value == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                builder.append(",\n");
            }
            indent(builder);
            builder.append(field.getName().substring(1));
            builder.append("=");
            if (value instanceof ItemModel) {
                builder.append(indentInnerToStringValue(value));
            } else {
                builder.append(value);
            }
        }
    }

    private static void indent(StringBuilder builder) {
        int depth = DEPTH.get();
        for (int i = 0; i < depth; i++) {
            builder.append("    ");
        }
    }

    private static boolean isFieldInteresting(Field field) {
        return field.getName().startsWith("_") && !Modifier.isStatic(field.getModifiers());
    }

    private static Object getFieldValue(ItemModel item, Field field) {
        field.setAccessible(true);
        try {
            return field.get(item);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String indentInnerToStringValue(Object value) {
        String innerToString = value.toString();
        innerToString = String.join("\n    ", innerToString.split("\n"));
        return innerToString;
    }

    /**
     * Function wrapper keeping track of seen objects.
     *
     * @see InMemoryItemModelAttribute#equals
     */
    private static <I, O> O executeWithSeen(Function<I, O> function, I input) {
        Set<Object> seen = SEEN.get();
        boolean recursionEntryPoint = false;
        if (seen == null) {
            SEEN.set(Collections.newSetFromMap(new IdentityHashMap<>()));
            recursionEntryPoint = true;
        }
        try {
            return function.apply(input);
        } finally {
            if (recursionEntryPoint) {
                SEEN.remove();
                DEPTH.remove();
            }
        }
    }
}
