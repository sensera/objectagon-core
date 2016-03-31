package org.objectagon.core.testutil;

import lombok.Getter;

import java.util.function.Consumer;

/**
 * Created by christian on 2016-03-31.
 */
public class ReceiveConsumer<V> implements Consumer<V> {
    @Getter private V v;
    @Override
    public void accept(V v) {
        this.v = v;
    }
}
