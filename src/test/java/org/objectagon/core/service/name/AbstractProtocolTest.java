package org.objectagon.core.service.name;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class AbstractProtocolTest {

    protected Result startTaskAndWaitForCompletion(Task task) throws FailedException {
        SynchronousTaskResult res = new SynchronousTaskResult();
        task.addFailedAction(res::failed);
        task.addSuccessAction(res::success);
        task.start();
        return res.waitForTaskCompletion(1000);
    }

    interface Result {
        Message.MessageName name();
        Iterable<Message.Value> values();
    }

    class FailedException extends Exception {
        ErrorClass errorClass;
        ErrorKind errorKind;
        Iterable<Message.Value> values;

        public FailedException(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values) {
            super(errorClass+": "+errorKind);
            this.errorClass = errorClass;
            this.errorKind = errorKind;
            this.values = values;
        }
    }

    private class SynchronousTaskResult  {
        boolean completed = false;
        boolean success = true;
        private ErrorClass errorClass;
        private ErrorKind errorKind;
        private Iterable<Message.Value> values;
        private Message.MessageName messageName;

        public void failed(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values) {
            this.errorClass = errorClass;
            this.errorKind = errorKind;
            this.values = values;
            success = false;
            completed();
        }

        public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
            this.messageName = messageName;
            this.values = values;
            success = true;
            completed();
        }

        protected synchronized void completed() {
            completed = true;
            notifyAll();
        }

        public synchronized Result waitForTaskCompletion(long timeout) {
            if (!completed) {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted!",e);
                }
                if (!completed)
                    throw new RuntimeException("Timeout!");
            }
            if (!success) {
                throw new RuntimeException(errorClass+": "+errorKind);
            }
            return new Result() {
                @Override public Message.MessageName name() {return messageName;}
                @Override public Iterable<Message.Value> values() {return values;}
            };
        }
    }

}
