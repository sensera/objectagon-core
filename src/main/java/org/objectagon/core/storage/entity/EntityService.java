package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<I extends Identity, D extends Data, B extends Receiver<I>, W extends EntityService.EntityServiceWorker> extends AbstractService<W,I,B> {


    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();

    public EntityService(StandardReceiverCtrl<B,I> receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.CREATE),
                (initializer, context) -> new StartServiceAction<W,I,B>((Service.ServiceActionCommands<W,I,B>)initializer, (W) context));
    }

    protected Entity<I,D> internalCreateEntity(EntityServiceWorker serviceWorker) {
        return null;
    }

    protected void createEntity(EntityServiceWorker serviceWorker) {
        Entity<I,D> entity = internalCreateEntity(serviceWorker);

        //serviceWorker.getValue(EntityServiceProtocol.FieldName.)
    }

    @Override
    public void addCompletedListener(W serviceWorker) {

    }

    public class EntityServiceWorker extends ServiceWorkerImpl {
        public EntityServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    private static class StartServiceAction<W extends Service.ServiceWorker, A extends Address, B extends Receiver<A>> extends AbstractServiceAction<W,A,B> {

        public StartServiceAction(Service.ServiceActionCommands<W,A,B> serviceActionCommands, W serviceWorker) {
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

            currentTask.addCompletedListener(context);
            currentTask.addCompletedActionListener(
                    (messageName, values) -> initializer.setStartedStatusAndClearCurrentTask(),
                    (errorClass, errorKind, values) -> initializer.setStoppedStatusAndClearCurrentTask()
            );
            currentTask.start();
            return Optional.empty();
        }
    }
}