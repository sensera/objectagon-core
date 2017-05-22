package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;

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
            action.run(this::success, this::failed);
        } catch (Throwable e) {
            e.printStackTrace();
            failed(ErrorClass.UNKNOWN, ErrorKind.UNEXPECTED, MessageValue.name(getName()), MessageValue.exception(e));
        }
    }

    @Override
    protected void handle(TaskWorker worker) {

    }
}
