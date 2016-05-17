package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

import java.util.Objects;

/**
 * Created by christian on 2015-10-11.
 */
public class NamedAddress implements Address, Name {
    private final Name name;

    public NamedAddress(Name name) {
        this.name = name;
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(StandardField.NAME).set(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedAddress)) return false;
        NamedAddress that = (NamedAddress) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "NamedAddress{" +
                "name=" + name +
                '}';
    }
}
