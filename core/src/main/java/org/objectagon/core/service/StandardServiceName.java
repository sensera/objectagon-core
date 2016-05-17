package org.objectagon.core.service;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;

import java.util.Objects;

/**
 * Created by christian on 2016-03-07.
 */

public class StandardServiceName implements Service.ServiceName {
    String name;

    public static StandardServiceName name(String name) {
        return new StandardServiceName(name);
    }

    @Override
    public String getName() {
        return name;
    }

    private StandardServiceName(String name) {
        this.name = name;
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(StandardField.NAME).set(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardServiceName)) return false;
        StandardServiceName that = (StandardServiceName) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "StandardServiceName{" +
                "name='" + name + '\'' +
                '}';
    }
}
