package org.objectagon.core.service;

/**
 * Created by christian on 2015-10-08.
 */
public interface Service {
    void start() throws FailedToStartServiceException;
    void stop() throws FailedToStopServiceException;
}
