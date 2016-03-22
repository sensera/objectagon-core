package feature.util;

import lombok.RequiredArgsConstructor;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-17.
 */
@RequiredArgsConstructor(staticName = "create")
public class TaskWait {
    private final Task task;
    private boolean completed = false;
    private Message.MessageName messageName;
    private Iterable<Message.Value> values;
    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;

    public Message startAndWait(long timeout) throws UserException {
        task.addFailedAction(this::failed)
            .addSuccessAction(this::success)
            .start();
        synchronized (this) {
            if (!completed) {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.TIMEOUT);
                }
                if (!completed)
                    throw new UserException(ErrorClass.UNKNOWN, ErrorKind.TIMEOUT);
                if (messageName==null)
                    throw new UserException(errorClass, errorKind, values);
            }
            return SimpleMessage.simple(messageName, values);
        }
    }

    private synchronized void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        this.messageName = messageName;
        this.values = values;
        completed = true;
        this.notifyAll();
    }

    private synchronized void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.values = values;
        completed = true;
        this.notifyAll();
    }

}
