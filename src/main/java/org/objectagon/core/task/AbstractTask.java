package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorker;

/**
 * Created by christian on 2015-10-11.
 */
public abstract class AbstractTask extends BasicReceiverImpl<Address, Task, Task.TaskCtrl, Task.TaskWorker> implements Task {

    private Status status = Status.Unstarted;
    private TaskName name;

    public TaskName getName() {return name;}

    public AbstractTask(TaskCtrl taskCtrl, TaskName name) {
        super(taskCtrl);
        this.name = name;
    }

    public final void start() {
        internalStart();
    }

    protected void failed(ErrorClass errorClass, ErrorKind errorKind) {
        //TODO send failed message
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

    @Override
    protected void handle(TaskWorker worker) {

    }

    @Override
    public void addCompletedListener(Action successAction, Action failedAction) {

    }


    @Override
    protected TaskWorker createWorker(WorkerContext workerContext) {
        return null;
    }


}
