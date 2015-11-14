package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorker;

/**
 * Created by christian on 2015-10-11.
 */
public abstract class AbstractTask extends BasicReceiverImpl<Address, Task.TaskCtrl, Task.TaskWorker> implements Task {

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
    public void addSuccessAction(Action successAction) {

    }

    @Override
    public void addFailedAction(Action failedAction) {

    }



}
