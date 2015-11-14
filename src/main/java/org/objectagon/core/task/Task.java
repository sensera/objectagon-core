package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.BasicWorker;

import java.util.function.Predicate;

/**
 * Created by christian on 2015-10-11.
 */
public interface Task extends BasicReceiver<Address> {

    enum Status {
        Unstarted, Initializing, Started, Verifying, Completed;
    }

    TaskName getName();

    void start();
    void addCompletedListener(CompletedListener completedListener);
    void addCompletedListener(Action successAction, Action failedAction);
    void addSuccessAction(Action successAction);
    void addFailedAction(Action failedAction);

    interface TaskCtrl extends BasicReceiverCtrl<Address> {
        void success(Message message);
        void failed(Message message);
    }

    interface TaskName extends Name {

    }

    interface CompletedListener {
        void success(Message message);
        void failed(StandardProtocol.ErrorKind errorKind, String description);
    }

    interface TaskWorker extends BasicWorker {

    }

    interface Action {
        void action(TaskWorker reply);
    }

}
