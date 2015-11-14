package org.objectagon.core.task;

import org.objectagon.core.msg.*;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTaskBuilder<U extends Protocol> extends AbstractTaskBuilder<StandardTask<U>, U> {

    public static <U extends Protocol> StandardTaskBuilder standard(Task.TaskName taskName, U session, Message.MessageName messageName) {
        return new StandardTaskBuilder<U>(new Task.TaskCtrl() {
            @Override
            public void success(Message message) {

            }

            @Override
            public void failed(Message message) {

            }

            @Override
            public Address createNewAddress(Receiver receiver) {
                return null;
            }

            @Override
            public void transport(Envelope envelope) {

            }
        }).createTask(taskName, session, messageName);
    }

    protected StandardTaskBuilder(Task.TaskCtrl taskCtrl) {
        super(taskCtrl);
    }

    @Override
    protected StandardTask<U> createTask(Task.TaskName taskName, Protocol<U> protocol, Message.MessageName messageName) {
        return new StandardTask<U>(taskCtrl, taskName, protocol, messageName);
    }
}
