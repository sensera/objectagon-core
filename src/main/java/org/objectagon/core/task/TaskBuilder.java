package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;

import java.util.Optional;

/**
 * Created by christian on 2015-11-03.
 */
public interface TaskBuilder {

    <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction);
    <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, StandardTask.SendMessageAction<U> sendMessageAction);
    <U extends Protocol.Session> Builder<ActionTask> action(Task.TaskName taskName, Action action);
    <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction);
    <U extends Protocol.Session> ChainedBuilder chain(Task.TaskName taskName, Action action);
    <U extends Protocol.Session> SequenceBuilder sequence(Task.TaskName taskName);

    Optional<Task> current();

    interface Builder<T extends Task> {
        Task start();
        Builder<T> success(Task.SuccessAction successAction);
        Builder<T> failed(Task.FailedAction failAction);
    }

    interface ChainedBuilder<T extends Task> extends Builder<T> {
        TaskBuilder next();
    }

    interface SequenceBuilder extends Builder<SequenceTask> {
        <U extends Protocol.Session> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<U> sendMessageAction);
        <U extends Protocol.Session> Builder<ActionTask> action(Task.TaskName taskName, Action action);
    }

    interface Action {
        void run();
    }


}
