package org.objectagon.core.task;

import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-11-08.
 */
public class ChainedTask extends AbstractTask {

    public ChainedTask(TaskCtrl taskCtrl) {
        super(taskCtrl);
    }

    @Override
    protected void internalStart() {

    }

    @Override
    protected void handle(TaskWorker worker) {

    }

    @Override
    protected TaskWorker createWorker(WorkerContext workerContext) {
        return null;
    }
}
