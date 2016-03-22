package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class FieldNameImpl implements Message.FieldName {
    private String name;

    public FieldNameImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldNameImpl fieldName = (FieldNameImpl) o;
        return Objects.equals(name, fieldName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
