package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicWorker;

import java.util.function.Predicate;

/**
 * Created by christian on 2015-10-11.
 */
public interface Task<A extends Address> extends BasicReceiver<A> {

    enum Status {
        Unstarted, Initializing, Started, Verifying, Completed;
    }

    enum MessageName implements Message.MessageName {
        COMPLETED
    }

    TaskName getName();

    void start();

    Task addSuccessAction(SuccessAction successAction);

    Task addFailedAction(FailedAction failedAction);

    interface TaskName extends Name { }

    interface CompletedListener extends SuccessAction, FailedAction { }

    interface TaskWorker extends BasicWorker { }

    interface SuccessAction {
        void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException;
    }

    interface FailedAction {
        void failed(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values);
    }
}
