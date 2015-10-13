package org.objectagon.core.tasks;

import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorker;

/**
 * Created by christian on 2015-10-11.
 */
public abstract class AbstractTask extends BasicReceiverImpl implements Task {

    private Status status = Status.Unstarted;
    private TaskName name;

    public TaskName getName() {return name;}

    public AbstractTask(TaskCtrl taskCtrl) {
        super(taskCtrl);
    }

    public final void start() {
        internalStart();
    }

    abstract protected void internalStart();

    public void addCompletedListener(CompletedListener completedListener) {

    }

    @Override
    protected void handle(BasicWorker worker) {

    }
}
