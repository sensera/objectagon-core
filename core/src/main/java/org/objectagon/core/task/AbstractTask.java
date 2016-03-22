package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-10-11.
 */
public abstract class AbstractTask<A extends Address> extends BasicReceiverImpl<A, Task.TaskWorker> implements Task<A> {

    private Status status = Status.Unstarted;
    private TaskName name;
    private List<SuccessAction> successActions = new LinkedList<>();
    private List<FailedAction> failedActions = new LinkedList<>();

    public TaskName getName() {return name;}

    public AbstractTask(Receiver.ReceiverCtrl taskCtrl, TaskName name) {
        super(taskCtrl);
        this.name = name;
    }

    public final void start() {
        System.out.println("AbstractTask.start "+name);
        status = Status.Started;
        try {
            internalStart();
        } catch (SevereError e) {
            failed(e.getErrorClass(), e.getErrorKind(), e.getParams());
        } catch (UserException e) {
            failed(e.getErrorClass(),e.getErrorKind());
        }
    }

    protected void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        System.out.println("AbstractTask.success "+name+" "+messageName);
        status = Status.Completed;
        intSuccess(messageName, values);
        successActions.stream().forEach(successAction -> {
            try {
                successAction.success(messageName, values);
            } catch (UserException e) {
                e.printStackTrace();
            }
        });
    }

    protected void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Message.Value... values) {
        failed(errorClass, errorKind, values != null ? Arrays.asList(values) : Collections.EMPTY_LIST);
    }

    protected void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        System.out.println("AbstractTask.failed "+name+" "+errorClass+" "+errorKind);
        status = Status.Completed;
        intFailed(errorClass, errorKind, values);
        failedActions.stream().forEach(failedAction -> failedAction.failed(errorClass, errorKind, values));
    }

    protected void intSuccess(Message.MessageName messageName, Iterable<Message.Value> values){}
    protected void intFailed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values){}

    abstract protected void internalStart() throws UserException;

    public void addCompletedListener(CompletedListener completedListener) {
        addSuccessAction(completedListener);
        addFailedAction(completedListener);
    }

    @Override
    public Task addSuccessAction(SuccessAction successAction) {
        successActions.add(successAction);
        return this;
    }

    @Override
    public Task addFailedAction(FailedAction failedAction) {
        failedActions.add(failedAction);
        return this;
    }

    @Override
    protected void handle(TaskWorker worker) {
        if (worker.isError()) {
            Message.Value errorClass = worker.getValue(StandardProtocol.FieldName.ERROR_CLASS);
            Message.Value errorKind = worker.getValue(StandardProtocol.FieldName.ERROR_KIND);
            Iterable<Message.Value> values = worker.getValues();
            failed(ErrorClass.valueOf(errorClass.asText()), ErrorKind.valueOf(errorKind.asText()), values);
            worker.setHandled();
        } else {
            success(worker.getMessageName(), worker.getValues());
            worker.setHandled();
        }
    }

    @Override
    protected TaskWorker createWorker(Receiver.WorkerContext workerContext) {
        return new TaskWorkerImpl(workerContext);
    }

    protected  class TaskWorkerImpl extends BasicWorkerImpl implements TaskWorker {
        public TaskWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }
    }


}
