package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2015-10-11.
 */
public class NamedAddress implements Address {
    Name name;

    public NamedAddress(Name name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedAddress that = (NamedAddress) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
