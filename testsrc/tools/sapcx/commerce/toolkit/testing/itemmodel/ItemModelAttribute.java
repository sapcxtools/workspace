package tools.sapcx.commerce.toolkit.testing.itemmodel;

import java.util.Locale;

interface ItemModelAttribute extends Cloneable {
    String getKey();
    Object getValue();
    void setValue(Object value);
    Object getValue(Locale locale);
    void setValue(Locale locale, Object value);
    ItemModelAttribute clone();
}
