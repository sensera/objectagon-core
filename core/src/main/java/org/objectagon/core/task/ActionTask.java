package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-03.
 */
public class ActionTask extends AbstractTask {
    private TaskBuilder.Action action;

    ActionTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, TaskBuilder.Action action) {
        super(taskCtrl, taskName);
        this.action = action;
    }

    @Override
    protected void internalStart() {
        try {
            action.run();
            success(StandardProtocol.MessageName.OK_MESSAGE, Message.NO_VALUES);
        } catch (Throwable e) {
            failed(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED, Message.NO_VALUES);
        }
    }

    @Override
    protected void handle(TaskWorker worker) {

    }
}
