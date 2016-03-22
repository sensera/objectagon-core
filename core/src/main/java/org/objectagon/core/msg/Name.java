package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-08.
 */
public interface Name {

    interface Named {
        Name getName();
    }

    interface GetName<T> {
        Named getNameFrom(T target);
    }
}
