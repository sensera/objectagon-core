package org.objectagon.core.utils;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Created by christian on 2017-03-22.
 */
public class WaitLock<R> {

    private static long DEFAULT_TIMEOUT = 60*1000; // 1 minute

    public static <R> WaitLock<R> create(long timeout) {
        return new WaitLock<R>(timeout);
    }

    public static <R> WaitLock<R> create() {
        return create(DEFAULT_TIMEOUT);
    }

    long timeout;
    R result;
    boolean completed = false;

    private WaitLock(long timeout) {
        this.timeout = timeout;
    }

    public void waitUntilCompleted() throws TimeoutException {
        get();
    }

    public void completed() {
        put(null);
    }

    public Optional<R> get() throws TimeoutException {
        synchronized (this) {
            if (completed) {
                return Optional.ofNullable(result);
            }
            try {
                wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!completed) {
                throw new TimeoutException("Timeout!");
            }
            return Optional.ofNullable(result);
        }
    }

    public void put(R result) {
        synchronized (this) {
            this.result = result;
            notifyAll();
        }
    }
}
