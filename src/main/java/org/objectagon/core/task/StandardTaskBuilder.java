package org.objectagon.core.task;

import org.objectagon.core.msg.*;

import java.util.Optional;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTaskBuilder implements TaskBuilder {

    Task.TaskCtrl taskCtrl;
    Hook hook;

    public StandardTaskBuilder(Task.TaskCtrl taskCtrl) {
        this(taskCtrl, null);
    }

    public StandardTaskBuilder(Task.TaskCtrl taskCtrl, Hook hook) {
        this.taskCtrl = taskCtrl;
        this.hook = hook;
    }

    @Override
    public <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction) {
        return new BuilderImpl<>(new StandardTask<>(taskCtrl, taskName, protocolName, target, sendMessageAction));
    }

    @Override
    public <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, StandardTask.SendMessageAction<U> sendMessageAction) {
        return new BuilderImpl<>(new StandardTask<>(taskCtrl, taskName, protocolName, composer, sendMessageAction));
    }

    @Override
    public Builder<ActionTask> action(Task.TaskName taskName, Action action) {
        return new BuilderImpl<>(new ActionTask(taskCtrl, taskName, action));
    }

    @Override
    public <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction) {
        return new ChainedBuilderImpl<>(new StandardTask<>(taskCtrl, taskName, protocolName, target, sendMessageAction));
    }

    @Override
    public <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Action action) {
        return new ChainedBuilderImpl<>(new ActionTask(taskCtrl, taskName, action));
    }

    @Override
    public <U extends Protocol.Session> SequenceBuilder sequence(Task.TaskName taskName) {
        return new SequenceBuilderImpl(new SequenceTask(taskCtrl, taskName));
    }

    @Override
    public Optional<Task> current() {
        if (hook==null)
            return Optional.empty();
        return Optional.of(hook.current());
    }

    private class BuilderImpl<S extends Task> implements Builder<S> {

        protected S task;

        public BuilderImpl(S task) {
            this.task = task;
            if (hook!=null) hook.createdTask(task);
        }

        @Override
        public Task start() {
            if (hook != null) {
                return hook.startParentTask();
            }
            task.start();
            return task;
        }

        @Override
        public Builder<S> success(Task.SuccessAction successAction) {
            task.addSuccessAction(successAction);
            return this;
        }

        @Override
        public Builder<S> failed(Task.FailedAction failAction) {
            task.addFailedAction(failAction);
            return this;
        }
    }

    private class ChainedBuilderImpl<T extends Task> extends BuilderImpl<T> implements ChainedBuilder<T>, Hook {

        public ChainedBuilderImpl(T task) {
            super(task);
        }

        @Override
        public TaskBuilder next() {
            return new StandardTaskBuilder(taskCtrl, this);
        }

        @Override
        public <B extends Task> void createdTask(B task) { task.addSuccessAction((messageName, values) -> task.start()); }

        @Override
        public Task startParentTask() {
            return start();
        }

        @Override
        public T current() {
            return task;
        }
    }

    public interface Hook {
        <T extends Task> void createdTask(T task);
        Task startParentTask();
        <T> T current();
    }

    private class SequenceBuilderImpl extends BuilderImpl<SequenceTask> implements SequenceBuilder {
        public SequenceBuilderImpl(SequenceTask task) {
            super(task);
        }

        @Override
        public <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction) {
            StandardTask<U> subTask = new StandardTask<>(taskCtrl, taskName, protocolName, target, sendMessageAction);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public <U extends Protocol.Session> Builder<ActionTask> action(Task.TaskName taskName, Action action) {
            ActionTask subTask = new ActionTask(taskCtrl, taskName, action);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }
    }


}
