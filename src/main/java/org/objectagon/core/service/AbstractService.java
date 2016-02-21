package org.objectagon.core.service;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;

/**
 * Created by christian on 2015-10-08.
 */
public abstract class AbstractService<W extends Service.ServiceWorker, A extends Address> extends StandardReceiverImpl<A, W> implements Service, Service.ServiceActionCommands<W> {

    private Status status = Status.Stopped;
    private Task currentTask;

    public AbstractService(ReceiverCtrl receiverCtrl) {
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

    protected Optional<TaskBuilder> internalCreateStartServiceTask(W serviceWorker) { return Optional.empty(); }
    protected Optional<TaskBuilder> internalCreateStopServiceTask(W serviceWorker) { return Optional.empty(); }

    public final Optional<Task> createStartServiceTask(W serviceWorker) {
        return internalCreateStartServiceTask(serviceWorker)
                .orElse(serviceWorker.getTaskBuilder())
                .current();
    }

    public final Optional<Task> createStopServiceTask(W serviceWorker) {
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
    public void addCompletedListener(W serviceWorker) {
        currentTask.addSuccessAction(serviceWorker);
        currentTask.addFailedAction(serviceWorker);
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
    public void transport(Envelope envelope) {
        getReceiverCtrl().transport(envelope);
    }

    /*************************************************************/
    /*                    Actions                                */
    /*************************************************************/

    static protected abstract class AbstractServiceAction<W extends Service.ServiceWorker> extends StandardAction<Service.ServiceActionCommands<W>, W> {

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
        protected Optional<Message.Value> internalRun() throws UserException {
            Optional<Task> currentTaskOptional = initializer.createStartServiceTask(context);

            if (!currentTaskOptional.isPresent()) {
                initializer.setStartedStatusAndClearCurrentTask();
                context.replyOk();
                return Optional.empty();
            }

            Task currentTask = currentTaskOptional.get();

            currentTask.addSuccessAction(context);
            currentTask.addFailedAction(context);
            currentTask.addSuccessAction( (messageName, values) -> initializer.setStartedStatusAndClearCurrentTask() );
            currentTask.addFailedAction((errorClass, errorKind, values) -> initializer.setStoppedStatusAndClearCurrentTask());
            currentTask.start();
            return Optional.empty();
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
        protected Optional<Message.Value> internalRun() throws UserException {

            Optional<Task> currentTaskOptional = initializer.createStopServiceTask(context);

            if (!currentTaskOptional.isPresent()) {
                initializer.setStoppedStatusAndClearCurrentTask();
                context.replyOk();
                return Optional.empty();
            }

            Task currentTask = currentTaskOptional.get();

            currentTask.addSuccessAction(context);
            currentTask.addFailedAction(context);
            currentTask.addSuccessAction( (messageName, values) -> initializer.setStoppedStatusAndClearCurrentTask() );
            currentTask.addFailedAction( (errorClass, errorKind, values) -> initializer.setStartedStatusAndClearCurrentTask() );

            currentTask.start();
            return Optional.empty();
        }
    }
}
