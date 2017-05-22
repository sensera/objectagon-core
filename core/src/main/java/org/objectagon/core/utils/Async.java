package org.objectagon.core.utils;

/**
 * Created by christian on 2017-04-15.
 */
public interface Async {

    <E extends Target> AsyncList<E> createAsyncList();

    interface AsyncList<E extends Target> {
        void add(E target) ;
        void remove(E target);
        void whenDone(WhenDone whenDone);
    }

    interface Target {
        void whenDone(WhenDone whenDone);
    }

    @FunctionalInterface
    interface WhenDone {
        void done();
    }


}
