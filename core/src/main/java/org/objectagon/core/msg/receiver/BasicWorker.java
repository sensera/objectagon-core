package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-11.
 */
public interface BasicWorker extends Receiver.Worker {
    Task.SuccessAction replyWithSelectedFieldFromResult(Message.Field field, ErrorClass onErrorClass);

    void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile);

    boolean isError();

    void start(Task task);
}
