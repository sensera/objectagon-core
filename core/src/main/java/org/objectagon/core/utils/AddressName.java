package org.objectagon.core.utils;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2017-04-23.
 */
public class AddressName<A extends Address, N extends Name> implements KeyValue<A,N> {

    public static <A extends Address, N extends Name> AddressName<A,N> create(A address, N name) {
        return new AddressName<>(address, name);
    }

    private A key;
    private N value;

    public AddressName(A address, N name) {
        this.key = address;
        this.value = name;
    }

    @Override public A getKey() {return key;}
    @Override public N getValue() {return value;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressName)) return false;
        AddressName<?, ?> that = (AddressName<?, ?>) o;
        return Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }
}
