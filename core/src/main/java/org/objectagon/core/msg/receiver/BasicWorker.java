package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-10-11.
 */
public interface BasicWorker extends Receiver.Worker {
    Task.SuccessAction replyWithSelectedFieldFromResult(Message.Field field, ErrorClass onErrorClass);

    void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile);

    boolean isError();

    boolean isDelayed();

    void start(Task task);

    TaskBuilder getTaskBuilder();

    void info(String message, Message.Value... values);
    void trace(String message, Message.Value... values);
    void error(String message, Message.Value... values);

    void ignoreWhenUnhandledError();
}
