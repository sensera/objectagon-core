package org.objectagon.core.utils;


/**
 * Created by christian on 2016-02-11.
 */
public abstract class AbstractStartStopController implements StartStopController {

    private Status status = Status.Stopped;

    @Override
    public Status getStatus() {
        return status;
    }

    public AbstractStartStopController() {}

    @Override
    public synchronized void start() throws FailedToStartException {
        if (Status.Running.equals(status))
            return ;
        protectedStart();
        status = Status.Running;
    }

    @Override
    public synchronized void stop() throws FailedToStopException {
        if (Status.Stopped.equals(status))
            return ;
        protectedStop();
        status = Status.Stopped;
    }

    protected abstract void protectedStart() throws FailedToStartException;
    protected abstract void protectedStop() throws FailedToStopException;

}
