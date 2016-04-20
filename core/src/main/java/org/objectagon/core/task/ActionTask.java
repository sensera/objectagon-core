package org.objectagon.core.task;

import org.objectagon.core.msg.Receiver;

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
        action.run();
    }

    @Override
    protected void handle(TaskWorker worker) {

    }
}
