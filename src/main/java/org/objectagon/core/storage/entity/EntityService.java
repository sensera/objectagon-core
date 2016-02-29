package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.*;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<I extends Identity, D extends Data, W extends EntityService.EntityServiceWorker> extends AbstractService<W,I> implements EntityServiceActionInitializer {

    private EntityName entityName;
    private Address persistencyService;

    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();

    public EntityService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<I> initializer) {
        super.initialize(serverId, timestamp, id, initializer);
        Object object = initializer.initialize(getAddress());
        EntityServiceConfig config = (EntityServiceConfig) object;
        entityName = config.getEntityName();
        persistencyService = config.getPersistencyService();
        getReceiverCtrl().registerFactory(entityName, createEntityFactory());
    }

    protected abstract Server.Factory createEntityFactory();

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.CREATE),
                (initializer, context) -> new CreateEntityAction<W,I,D>((EntityService.this), (W) context));
    }

    protected abstract DataVersion createInitialDataFromValues(I identity, Version version, Iterable<Message.Value> values);

    @Override
    public void addCompletedListener(W serviceWorker) {

    }

    @Override
    public Address getPersistencyService() {
        return persistencyService;
    }

    public class EntityServiceWorker extends ServiceWorkerImpl {
        public EntityServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
        public EntityName getEntityName() {return entityName;}

        public DataVersion getInitializeEntityWithValues(Version version) {
            return createInitialDataFromValues(getAddress(), version, getValues());
        }

        public Version getVersionValue() {
            return StandardVersion.create(getValue(Version.VERSION));
        }

        public Task persistDataVersion(DataVersion dataVersion) {
            PersistenceServiceProtocol.Send send = createTargetSession(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, persistencyService);
            return send.pushDataVersion(dataVersion);
        }
    }

    private static class CreateEntityAction<W extends EntityService.EntityServiceWorker, I extends Identity, D extends Data> extends AsyncAction<EntityServiceActionInitializer, W> {

        Version version;
        DataVersion dataVersion;

        public CreateEntityAction(EntityServiceActionInitializer initializer, W context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            version = context.getVersionValue();
            dataVersion = context.getInitializeEntityWithValues(version);
            return super.initialize();
        }

        @Override
        protected Task internalRun(W context) throws UserException {
            Initializer<I> entityInitializer = new Initializer<I>() {
                @Override
                public <C extends SetInitialValues> C initialize(I address) {
                    return (C) new Entity.EntityConfig() {
                        @Override public DataVersion getDataVersion() {return dataVersion;}
                        @Override public Address getPersistenceAddress() {
                            return initializer.getPersistencyService();
                        }
                    };
                }
            };

            Entity<I,D> entity = context.createReceiver(context.getEntityName(), entityInitializer);
            if (entity==null)
                throw new NullPointerException("entity is null!");

            setSuccessAction((messageName, values) -> context.replyWithParam(MessageValue.address(entity.getAddress())));

            return context.persistDataVersion(dataVersion);
        }
    }

    protected interface EntityServiceConfig extends Receiver.SetInitialValues {
        EntityName getEntityName();
        Address getPersistencyService();
    }
}
