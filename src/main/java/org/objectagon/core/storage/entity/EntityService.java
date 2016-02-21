package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.*;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<I extends Identity, D extends Data, W extends EntityService.EntityServiceWorker> extends AbstractService<W,I> {

    private EntityName entityName;

    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();

    public EntityService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<I> initializer) {
        super.initialize(serverId, timestamp, id, initializer);
        Object object = initializer.initialize(getAddress());
        System.out.println("EntityService.initialize "+object.getClass().getName());
        EntityServiceConfig config = (EntityServiceConfig) object;
        entityName = config.getEntityName();
        getReceiverCtrl().registerFactory(entityName, createEntityFactory());
    }

    protected abstract Server.Factory createEntityFactory();

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.CREATE),
                (initializer, context) -> new CreateEntityAction<W,I,D>((Service.ServiceActionCommands<W>)initializer, (W) context));
    }

    protected abstract D createInitialDataFromValues(I identity, Version version, Iterable<Message.Value> values);

    @Override
    public void addCompletedListener(W serviceWorker) {

    }

    public class EntityServiceWorker extends ServiceWorkerImpl {
        public EntityServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
        public EntityName getEntityName() {return entityName;}

        public Iterable<D> getInitializeEntityWithValues(Version version) {
            return Arrays.asList(createInitialDataFromValues(getAddress(), version, getValues()));
        }

        public Version getVersionValue() {
            return StandardVersion.create(getValue(Version.VERSION));
        }
    }



    private static class CreateEntityAction<W extends EntityService.EntityServiceWorker, I extends Identity, D extends Data> extends AbstractServiceAction<W> {

        Version version;
        Address persistencyService;

        public CreateEntityAction(Service.ServiceActionCommands<W> serviceActionCommands, W serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            version = context.getVersionValue();
            persistencyService = context.getValue(EntityServiceProtocol.FieldName.PERSISTENCY_SERVICE).asAddress();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {


            Initializer<I> entityInitializer = new Initializer<I>() {
                @Override
                public <C extends SetInitialValues> C initialize(I address) {
                    return (C) new Entity.EntityConfig() {
                        @Override
                        public <D extends Data> Iterable<D> getDatas() {
                            return context.getInitializeEntityWithValues(version);
                        }

                        @Override
                        public Address getPersistenceAddress() {
                            return persistencyService;
                        }
                    };
                }
            };

            Entity<I,D> entity = context.createReceiver(context.getEntityName(), entityInitializer);
            if (entity==null)
                throw new NullPointerException("entity is null!");

            return Optional.of(VolatileAddressValue.address(entity.getAddress()));
        }
    }

    protected interface EntityServiceConfig extends Receiver.SetInitialValues {
        EntityName getEntityName();
    }
}
