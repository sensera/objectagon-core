package org.objectagon.core.utils;

/**
 * Created by christian on 2016-02-11.
 */
public interface StartStopController {
    enum Status { Running, Stopped}

    Status getStatus();

    void start() throws FailedToStartException;
    void stop() throws FailedToStopException;

}
