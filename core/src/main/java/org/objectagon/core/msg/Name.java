package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-08.
 */
public interface Name {

    interface Named<N extends Name> {
        N getName();
    }

    interface GetName<T, N extends Name> {
        Named<N> getNameFrom(T target);
    }
}
