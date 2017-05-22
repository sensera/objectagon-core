package org.objectagon.core.object.field;

import org.objectagon.core.object.Field;

import java.util.Objects;

/**
 * Created by christian on 2016-03-19.
 */
public class FieldNameImpl implements Field.FieldName {

    public static FieldNameImpl create(String name) {
        return new FieldNameImpl(name);
    }

    private final String name;

    private FieldNameImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldNameImpl)) return false;
        FieldNameImpl fieldName = (FieldNameImpl) o;
        return Objects.equals(getName(), fieldName.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
