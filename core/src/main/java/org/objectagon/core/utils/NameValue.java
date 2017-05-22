package org.objectagon.core.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2017-04-23.
 */
public class NameValue<N extends Name, V extends Message.Value> implements KeyValue<N,V> {

    public static <N extends Name, V extends Message.Value> NameValue<N,V> create(N name, V value) {
        return new NameValue<>(name, value);
    }

    private N key;
    private V value;

    public NameValue(N key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public N getKey() {return key;}
    @Override public V getValue() {return value;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NameValue)) return false;
        NameValue<?, ?> nameValue = (NameValue<?, ?>) o;
        return Objects.equals(getKey(), nameValue.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }
}
