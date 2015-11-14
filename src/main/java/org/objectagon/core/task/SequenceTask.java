package org.objectagon.core.task;

/**
 * Created by christian on 2015-11-08.
 */
public class SequenceTask extends AbstractTask {

    public SequenceTask(TaskCtrl taskCtrl) {
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
