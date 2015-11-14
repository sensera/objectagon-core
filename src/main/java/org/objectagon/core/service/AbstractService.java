package org.objectagon.core.service;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;

/**
 * Created by christian on 2015-10-08.
 */
public abstract class AbstractService<W extends Service.ServiceWorker> extends StandardReceiverImpl<StandardAddress, StandardReceiverCtrl<StandardAddress>, W> implements Service, Service.ServiceActionCommands<W> {

    private Status status = Status.Stopped;
    private Task currentTask;

    public AbstractService(StandardReceiverCtrl<StandardAddress> receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(ServiceProtocol.MessageName.START_SERVICE),
                (initializer, context) -> new StartServiceAction<W>( (Service.ServiceActionCommands) initializer, (W) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(ServiceProtocol.MessageName.STOP_SERVICE),
                (initializer, context) -> new StopServiceAction<W>( (Service.ServiceActionCommands) initializer, (W) context)
        );
    }

    @Override
    protected Reactor.ActionInitializer getActionInitializer(W worker) {
        return this;
    }

    protected Optional<TaskBuilder> internalCreateStartServiceTask(ServiceWorker serviceWorker) { return Optional.empty(); }
    protected Optional<TaskBuilder> internalCreateStopServiceTask(ServiceWorker serviceWorker) { return Optional.empty(); }

    public final Optional<Task> createStartServiceTask(ServiceWorker serviceWorker) {
        return internalCreateStartServiceTask(serviceWorker)
                .orElse(serviceWorker.getTaskBuilder())
                .current();
    }

    public final Optional<Task> createStopServiceTask(ServiceWorker serviceWorker) {
        return internalCreateStopServiceTask(serviceWorker)
                .orElse(serviceWorker.getTaskBuilder())
                .current();
    }

    /*************************************************************/
    /* Implementation of Interface Service.ServiceActionCommands */
    /*************************************************************/

    @Override
    public boolean isStarting() {
        return status==Status.Starting;
    }

    @Override
    public boolean isStopping() {
        return status==Status.Stopping;
    }

    @Override
    public boolean isStarted() {
        return status==Status.Started;
    }

    @Override
    public boolean isStopped() {
        return status == Status.Stopped;
    }

    @Override
    public void addCompletedListener(ServiceWorker serviceWorker) {
        currentTask.addCompletedListener(serviceWorker);
    }

    @Override
    public void setStartedStatusAndClearCurrentTask() {
        status = Status.Started;
        currentTask = null;
    }

    @Override
    public void setStoppedStatusAndClearCurrentTask() {
        status = Status.Stopped;
        currentTask = null;
    }

    @Override
    public StandardAddress createNewAddress(Receiver receiver) {
        return getReceiverCtrl().createNewAddress(receiver);
    }

    @Override
    public void transport(Envelope envelope) {
        getReceiverCtrl().transport(envelope);
    }

    /*************************************************************/
    /*                    Actions                                */
    /*************************************************************/

    private static abstract class AbstractServiceAction<W extends Service.ServiceWorker> extends StandardAction<Service.ServiceActionCommands<W>, W> {

        public AbstractServiceAction(Service.ServiceActionCommands<W> serviceActionCommands, W serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

    }

    private static class StartServiceAction<W extends Service.ServiceWorker> extends AbstractServiceAction<W> {

        public StartServiceAction(Service.ServiceActionCommands<W> serviceActionCommands, W serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() {
            if (initializer.isStarting()) {
                initializer.addCompletedListener(context);
                return false;
            }

            if (initializer.isStopping()) {
                context.replyWithError(ServiceProtocol.ErrorKind.ServiceIsStoppning);
                return false;
            }

            if (initializer.isStarted()) {
                context.replyOk();
                return false;
            }
            return true;
        }

        @Override
        public void run() {
            Optional<Task> currentTaskOptional = initializer.createStartServiceTask(context);

            if (!currentTaskOptional.isPresent()) {
                initializer.setStartedStatusAndClearCurrentTask();
                context.replyOk();
                return;
            }

            Task currentTask = currentTaskOptional.get();

            currentTask.addCompletedListener(context);
            currentTask.addCompletedListener(
                    success -> initializer.setStartedStatusAndClearCurrentTask(),
                    fail -> initializer.setStoppedStatusAndClearCurrentTask()
            );
            currentTask.start();
        }
    }

    private static class StopServiceAction<W extends Service.ServiceWorker> extends AbstractServiceAction<W> {

        public StopServiceAction(Service.ServiceActionCommands<W> serviceActionCommands, W serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() {
            if (initializer.isStopping()) {
                initializer.addCompletedListener(context);
                return false;
            }

            if (initializer.isStarting()) {
                context.replyWithError(ServiceProtocol.ErrorKind.ServiceIsStarting);
                return false;
            }

            if (initializer.isStopped()) {
                context.replyOk();
                return false;
            }
            return true;
        }

        @Override
        public void run() {

            Optional<Task> currentTaskOptional = initializer.createStopServiceTask(context);

            if (!currentTaskOptional.isPresent()) {
                initializer.setStoppedStatusAndClearCurrentTask();
                context.replyOk();
                return;
            }

            Task currentTask = currentTaskOptional.get();

            currentTask.addCompletedListener(context);
            currentTask.addCompletedListener(
                    success -> initializer.setStoppedStatusAndClearCurrentTask(),
                    fail -> initializer.setStartedStatusAndClearCurrentTask()
            );

            currentTask.start();
        }
    }
}
