package org.objectagon.core.testutil;

import java.util.function.Consumer;

/**
 * Created by christian on 2016-03-31.
 */
public class ReceiveConsumer<V> implements Consumer<V> {
    private V v;
    @Override
    public void accept(V v) {
        this.v = v;
    }

    public V getV() {
        return v;
    }
}
