package org.objectagon.core.task;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.name.StandardName;

/**
 * Created by christian on 2017-04-10.
 */
public class TaskFailed {
    public static Task failedWithUserException(UserException e) {
        return new Task(){
            @Override public Task addFailedAction(FailedAction failedAction) {
                failedAction.failed(e.getErrorClass(), e.getErrorKind(), e.getParams());
                return this;
            }
            @Override public void start() {}
            @Override public void trace() {}
            @Override public Task addSuccessAction(SuccessAction successAction) {return this;}
            @Override public Task addFirstSuccessAction(SuccessAction successAction) {return this;}
            @Override public Name getName() {return StandardName.name(e.getErrorClass().name());}
            @Override public Address getAddress() {return null;}
            @Override public void receive(Envelope envelope) {}
            @Override public void configure(Configurations... configurations) {}
        };
    }


}
