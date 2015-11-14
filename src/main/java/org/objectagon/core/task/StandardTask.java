package org.objectagon.core.task;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTask<U extends Protocol> extends AbstractTask {
    public StandardTask(TaskCtrl taskCtrl, TaskName taskName, U protocol, Message.MessageName messageName) {
        super(taskCtrl);
    }

    @Override
    protected void handle(TaskWorker worker) {

    }

    @Override
    protected TaskWorker createWorker(WorkerContext workerContext) {
        return null;
    }

    @Override
    protected void internalStart() {

    }
}
