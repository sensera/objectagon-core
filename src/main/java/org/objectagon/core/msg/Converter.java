package org.objectagon.core.msg;

/**
 * Created by christian on 2016-02-26.
 */
public interface Converter<D> {
    ToData<D> toData();
    FromData<D> fromData();

    @FunctionalInterface
    interface ToData<D> {
        D toData(Message.Value values);
    }

    @FunctionalInterface
    interface FromData<D> {
        Message.Value fromData(D data);
    }
}
