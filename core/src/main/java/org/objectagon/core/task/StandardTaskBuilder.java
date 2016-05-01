package org.objectagon.core.task;

import lombok.Data;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.ResolveTargetComposer;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.AbstractReceiver;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.*;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTaskBuilder extends AbstractReceiver<StandardTaskBuilderAddress> implements TaskBuilder {

    public final static Name STANDARD_TASK_BUILDER = StandardName.name("STANDARD_TASK_BUILDER");
    public final static Name HOOK_CONFIG_NAME = StandardName.name("HOOK_CONFIG_NAME");

    public static void registerAt(Server server) {
        server.registerFactory(STANDARD_TASK_BUILDER, StandardTaskBuilder::new);
    }

    public static StandardTaskBuilder create(Server.CreateReceiverByName createReceiverByName) {
        return createReceiverByName.createReceiver(STANDARD_TASK_BUILDER);
    }

    public static StandardTaskBuilder chain(Server.CreateReceiverByName createReceiverByName, Hook hook) {
        return createReceiverByName.createReceiver(STANDARD_TASK_BUILDER, OneReceiverConfigurations.create(HOOK_CONFIG_NAME, HookConfig.hook(hook)));
    }

    private TasksByAddress tasksByAddress = new TasksByAddress();
    private TaskBuilderReceiverCtrl taskBuilderReceiverCtrl;
    private long sequenceIdCounter = 0;
    private Optional<Hook> hook = Optional.empty();
    private List<Message.Value> headers = new ArrayList<>();

    private Message.Values headers() { return () -> headers;}

    @Override
    public void addHeader(Message.Value value) {
        headers.add(value);
    }

    private StandardTaskBuilder(Receiver.ReceiverCtrl taskCtrl) {
        super(taskCtrl);
        taskBuilderReceiverCtrl = new TaskBuilderReceiverCtrl();
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        FindNamedConfiguration.finder(configurations).<HookConfig>getConfigurationByNameOptional(HOOK_CONFIG_NAME)
                .ifPresent(hookConfig -> hook = Optional.ofNullable(hookConfig.getHook()));
    }

    @Override
    protected StandardTaskBuilderAddress createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardTaskBuilderAddress::new);
    }

    @Override
    public void receive(Envelope envelope) {
        envelope.targets(tasksByAddress::get);
    }

    private void createdTask(Task task) {
        if (task.getAddress()==null) {
            task.configure(OneReceiverConfigurations.create(Receiver.ADDRESS_CONFIGURATIONS, TaskAddressConfigurationParameters.address(
                    getAddress(),
                    (Task.TaskName) task.getName(), // FIXME: 2016-04-11
                    sequenceIdCounter++
            )));
            task.addFailedAction((errorClass, errorKind, values) -> tasksByAddress.remove(task));
            task.addSuccessAction((messageName, values) -> tasksByAddress.remove(task));
            tasksByAddress.add(task);
        }
    }

    @Override
    public <S extends Protocol.Send> Builder<ProtocolTask<S>> protocol(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction) {
        return new BuilderImpl<>(new ProtocolTask<S>(taskBuilderReceiverCtrl, taskName, protocolName, target, sendMessageAction, headers()));
    }

    @Override
    public <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction) {
        Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
        S send = (S) protocol.createSend(() -> StandardComposer.create(this, target, headers()));
        return new BuilderImpl<>(sendMessageAction.run(send));
    }

    @Override
    public <S extends Protocol.Send> Builder<StandardTask<S>> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction) {
        return new BuilderImpl<>(new StandardTask<>(taskBuilderReceiverCtrl, taskName, protocolName, target, sendMessageAction, headers()));
    }

    @Override
    public <S extends Protocol.Send> Builder<StandardTask<S>> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, StandardTask.SendMessageAction<S> sendMessageAction) {
        return new BuilderImpl<>(new StandardTask<>(taskBuilderReceiverCtrl, taskName, protocolName, composer, sendMessageAction));
    }

    @Override
    public Builder<ActionTask> action(Task.TaskName taskName, Action action) {
        return new BuilderImpl<>(new ActionTask(taskBuilderReceiverCtrl, taskName, action));
    }

    @Override
    public <S extends Protocol.Send> ChainedBuilder chain(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction) {
        return new ChainedBuilderImpl<>(new StandardTask<>(taskBuilderReceiverCtrl, taskName, protocolName, target, sendMessageAction, headers()));
    }

    @Override
    public ChainedBuilder chain(Task.TaskName taskName, Action action) {
        return new ChainedBuilderImpl<>(new ActionTask(taskBuilderReceiverCtrl, taskName, action));
    }

    @Override
    public SequenceBuilder sequence(Task.TaskName taskName) {
        return new SequenceBuilderImpl(new SequenceTask(taskBuilderReceiverCtrl, taskName));
    }

    @Override
    public Optional<Task> current() {
        return hook.map(Hook::current);
    }

    private class BuilderImpl<S extends Task> implements Builder<S> {

        protected S task;

        public BuilderImpl(S task) {
            createdTask(task);
            this.task = task;
            hook.ifPresent(hook -> hook.createdTask(task));
        }

        @Override
        public Task create() {
            return task;
        }

        @Override
        public Task start() {
            return hook.map(Hook::startParentTask)
                    .orElseGet(() -> {
                        task.start();
                        return task;
                    });
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
            return chain(getReceiverCtrl(), this);
        }

        @Override
        public <B extends Task> void createdTask(B task) { task.addSuccessAction((messageName, values) -> task.start()); }

        @Override
        public Task startParentTask() {
            return start();
        }

        @Override
        public <T> T current() {
            return (T) task;
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
        public <S extends Protocol.Send> Builder<StandardTask> message(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, StandardTask.SendMessageAction<S> sendMessageAction) {
            StandardTask<S> subTask = new StandardTask<>(taskBuilderReceiverCtrl, taskName, protocolName, target, sendMessageAction);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
            S send = (S) protocol.createSend(() -> StandardComposer.create(task, target, headers()));
            Task subTask = sendMessageAction.run(send);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public <S extends Protocol.Send> Builder<Task> protocol(Protocol.ProtocolName protocolName, Composer.ResolveTarget target, ProtocolTask.SendMessageAction<S> sendMessageAction) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
            S send = (S) protocol.createSend(() -> ResolveTargetComposer.create(task, target, headers()));
            Task subTask = sendMessageAction.run(send);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public Builder<ActionTask> action(Task.TaskName taskName, Action action) {
            ActionTask subTask = new ActionTask(taskBuilderReceiverCtrl, taskName, action);
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public <S extends Protocol.Send> Builder<Task> addTask(Task subTask) {
            task.add(subTask);
            return new BuilderImpl<>(subTask);
        }

        @Override
        public <S extends Protocol.Send> Builder<Task> addTask(SequenceTask.TaskSupplier taskSupplier) {
            task.add( (msg, values) -> {
                Optional<Task> newTaskOpt = taskSupplier.createTask(msg, values);
                newTaskOpt.ifPresent(StandardTaskBuilder.this::createdTask);
                return newTaskOpt;
            });
            return null;
        }
    }

    private class TaskBuilderReceiverCtrl implements ReceiverCtrl {
        @Override
        public void registerFactory(Name name, Server.Factory factory) {
            getReceiverCtrl().registerFactory(name, factory);
        }

        @Override
        public Server.ServerId getServerId() {
            return getReceiverCtrl().getServerId();
        }

        @Override
        public void registerAliasForAddress(Name name, Address address) {
            getReceiverCtrl().registerAliasForAddress(name, address);
        }

        @Override
        public void removeAlias(Name name) {
            getReceiverCtrl().removeAlias(name);
        }

        @Override
        public Optional<Address> lookupAddressByAlias(Name name) {
            return getReceiverCtrl().lookupAddressByAlias(name);
        }

        @Override
        public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Configurations... configurations) {
            return getReceiverCtrl().createReceiver(name, configurations);
        }

        @Override
        public Receiver create(ReceiverCtrl receiverCtrl) {
            return getReceiverCtrl().create(receiverCtrl);
        }

        @Override
        public void transport(Envelope envelope) {
            getReceiverCtrl().transport(envelope);
        }
    }

    private static class TasksByAddress {
        private Map<Address, Transporter> tasksByAddress = new HashMap<>();


        public Optional<Transporter> get(Address address) {
            return Optional.ofNullable(tasksByAddress.get(address));
        }

        public void add(Task task) {
            tasksByAddress.put(task.getAddress(), task::receive);
        }

        public void remove(Task task) {
            tasksByAddress.remove(task.getAddress());
        }
    }

    @Data(staticConstructor = "hook")
    private static class HookConfig implements Receiver.NamedConfiguration {
        private final Hook hook;
    }

    @Data(staticConstructor = "address")
    public static class TaskAddressConfigurationParameters implements Receiver.NamedConfiguration {
        private final TaskBuilderAddress taskBuilderAddress;
        private final Task.TaskName name;
        private final long taskSequenceId;
    }

}
