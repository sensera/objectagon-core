package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;

import java.util.Optional;

/**
 * Created by christian on 2015-11-03.
 */
public interface TaskBuilder {

    <S extends Protocol.Send> Builder<ProtocolTask<S>> protocol(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction);
    <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction);
    <S extends Protocol.Send> Builder<StandardTask<S>> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction);
    <S extends Protocol.Send> Builder<StandardTask<S>> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, StandardTask.SendMessageAction<S> sendMessageAction);
    Builder<ActionTask> action(Task.TaskName taskName, Action action);
    <S extends Protocol.Send> ChainedBuilder chain(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction);
    ChainedBuilder chain(Task.TaskName taskName, Action action);
    SequenceBuilder sequence(Task.TaskName taskName);

    void addHeader(Message.Value value);

    Optional<Task> current();

    interface Builder<T extends Task> {
        Task create();
        Task start();
        Builder<T> success(Task.SuccessAction successAction);
        Builder<T> failed(Task.FailedAction failAction);
    }

    interface ChainedBuilder<T extends Task> extends Builder<T> {
        TaskBuilder next();
    }

    interface SequenceBuilder extends Builder<SequenceTask> {
        <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction);
        <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Composer.ResolveTarget target, ProtocolTask.SendMessageAction<S> sendMessageAction);
        <S extends Protocol.Send> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction);
        Builder<ActionTask> action(Task.TaskName taskName, Action action);
        <S extends Protocol.Send> Builder<Task> addTask(Task task);
        <S extends Protocol.Send> Builder<Task> addTask(SequenceTask.TaskSupplier task);
    }

    interface Action {
        void run();
    }

    interface TaskBuilderAddress extends Address {
        Task.TaskAddress create(Task.TaskName taskName, long taskSequenceId);
    }
}
