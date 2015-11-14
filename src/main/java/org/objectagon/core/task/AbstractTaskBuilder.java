package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;

/**
 * Created by christian on 2015-11-01.
 */
public abstract class AbstractTaskBuilder<T extends Task, U extends Protocol> implements TaskBuilder<T,U> {

    Task.TaskCtrl taskCtrl;

    protected AbstractTaskBuilder(Task.TaskCtrl taskCtrl) {
        this.taskCtrl = taskCtrl;
    }

    protected abstract T createTask(Task.TaskName taskName, Protocol<U> protocol, Message.MessageName messageName);

    @Override
    public Builder<T> builder(Task.TaskName taskName, Protocol<U> protocol, Message.MessageName messageName) {
        return new BuilderImpl(createTask(taskName, protocol, messageName));
    }

    private class BuilderImpl implements TaskBuilder.Builder<T> {
        T task;

        public BuilderImpl(T task) {
            this.task = task;
        }

        @Override public T start() {return task;}

        @Override
        public Builder<T> addSuccess(Task.Action succesAction) {
            task.addSuccessAction(succesAction);
            return this;
        }

        @Override
        public Builder<T> addFail(Task.Action failAction) {
            task.addFailedAction(failAction);
            return this;
        }
    }


}
