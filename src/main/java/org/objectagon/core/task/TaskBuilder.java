package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;

import java.util.Optional;

/**
 * Created by christian on 2015-11-03.
 */
public interface TaskBuilder {

    <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol<U> protocol, Address target, SendMessageAction<U> sendMessageAction);
    <U extends Protocol.Session> Builder<ActionTask> action(Task.TaskName taskName, Action action);
    <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Protocol<U> protocol, Address target, SendMessageAction<U> sendMessageAction);
    <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Action action);
    <U extends Protocol.Session> SequenceBuilder sequence(Task.TaskName taskName);

    interface Builder<T extends Task> {
        Task start();
        Builder<T> addSuccess(Task.Action succesAction);
        Builder<T> addFail(Task.Action failAction);
    }

    interface ChainedBuilder<T extends Task> extends Builder<T> {
        TaskBuilder next();
    }

    interface SequenceBuilder extends Builder<SequenceTask> {
        <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol<U> protocol, Address target, SendMessageAction<U> sendMessageAction);
        <U extends Protocol.Session> Builder<ActionTask> action(Task.TaskName taskName, Action action);
    }

    interface Action {
        void run();
    }

    interface SendMessageAction<U extends Protocol.Session> {
        void run(U session);
    }

}
