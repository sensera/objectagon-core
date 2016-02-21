package org.objectagon.core.utils;

/**
 * Created by christian on 2016-01-25.
 */
public class IdCounter {
    private long counter;

    public IdCounter(long counter) {
        this.counter = counter;
    }

    public synchronized long next() {
        return counter++;
    }
}
