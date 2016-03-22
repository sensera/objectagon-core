package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<A extends Service.ServiceName, I extends Identity, D extends Data, W extends EntityService.EntityServiceWorker> extends AbstractService<W,A> implements EntityServiceActionInitializer {

    private EntityName entityName;
    private ServiceName persistencyService;

    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();

    public EntityService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<A> initializer) {
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
                (initializer, context) -> new CreateEntityAction<W, I, D>((EntityService.this), (W) context));
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.GET),
                (initializer, context) -> new GetEntityAction<W, I, D>((EntityService.this), (W) context));
    }

    protected abstract DataVersion<I,StandardVersion> createInitialDataFromValues(I identity, Message.Values initialParams);

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

        public DataVersion<I,StandardVersion> getInitializeEntityWithValues(I identity, Message.Values initialParams) {
            return createInitialDataFromValues(identity, initialParams);
        }

        Transaction currentTransaction() {
            return getWorkerContext().getHeader(Transaction.TRANSACTION).asAddress();
        }

        public Version getVersionValue() {
            return StandardVersion.create(getValue(Version.VERSION));
        }

        public Task persistDataVersion(DataVersion dataVersion) {
            PersistenceServiceProtocol.Send send = createTargetSession(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, persistencyService);
            return send.pushDataVersion(dataVersion);
        }

        public Task getLatestDataVersionFromPeristence(I identity) {
            PersistenceServiceProtocol.Send send = createTargetSession(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, persistencyService);
            return send.getLatestDataVersion(identity);
        }
    }

    private static class CreateEntityAction<W extends EntityService.EntityServiceWorker, I extends Identity, D extends Data> extends AsyncAction<EntityServiceActionInitializer, W> implements Consumer<DataVersion<I,StandardVersion>> {

        private Message.Values initialParams;
        private DataVersion dataVersion;

        public CreateEntityAction(EntityServiceActionInitializer initializer, W context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            initialParams = context.getValue(StandardField.VALUES).asValues();
            return super.initialize();
        }

        @Override
        public void accept(DataVersion<I, StandardVersion> dataVersion) {
            this.dataVersion = dataVersion;
        }

        @Override
        protected Task internalRun(W context) throws UserException {
            Initializer<I> entityInitializer = createInitializer(context, this);

            Entity<I,D> entity = context.createReceiver(context.getEntityName(), entityInitializer);
            if (entity==null)
                throw new NullPointerException("entity is null!");

            setSuccessAction((messageName, values) -> context.replyWithParam(MessageValue.address(entity.getAddress())));

            return context.persistDataVersion(dataVersion);
        }

        private Initializer<I> createInitializer(W context, final Consumer<DataVersion<I, StandardVersion>> dataVersionConsumer) {
            return new Initializer<I>() {
                        @Override
                        public <C extends SetInitialValues> C initialize(I address) {
                            return (C) new Entity.EntityConfig() {
                                @Override public DataVersion getDataVersion() {
                                    DataVersion dataVersion = context.getInitializeEntityWithValues(address, initialParams);
                                    dataVersionConsumer.accept(dataVersion);
                                    return dataVersion;}
                                @Override public long getDataVersionCounter() {return 0;}
                            };
                        }
                    };
        }
    }


    private static class GetEntityAction<W extends EntityService.EntityServiceWorker, I extends Identity, D extends Data> extends AsyncAction<EntityServiceActionInitializer, W> {

        I identifier;

        public GetEntityAction(EntityServiceActionInitializer initializer, W context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            identifier = context.getValue(Identity.IDENTITY).asAddress();
            return super.initialize();
        }

        @Override
        protected Task internalRun(W context) throws UserException {
            Task latestDataVersionFromPeristence = context.getLatestDataVersionFromPeristence(identifier);

            setSuccessAction((messageName1, values) -> {
                MessageValueFieldUtil fieldUtil = MessageValueFieldUtil.create(values);
                DataVersion dataVersion = fieldUtil.getValueByField(DataVersion.DATA_VERSION).getValue();
                Long counter = fieldUtil.getValueByField(DataVersion.DATA_VERSION_COUNTER).asNumber();
                Initializer<I> entityInitializer = createInitializer(dataVersion, counter);
                Entity<I, D> entity = context.createReceiver(context.getEntityName(), entityInitializer);
                if (entity == null)
                    throw new NullPointerException("entity is null!");
                context.replyWithParam(MessageValue.address(entity.getAddress()));
            });

            return latestDataVersionFromPeristence;
        }

        private Initializer<I> createInitializer(final DataVersion dataVersion, final Long counter) {
            return new Initializer<I>() {
                        @Override
                        public <C extends SetInitialValues> C initialize(I address) {
                            return (C) new Entity.EntityConfig() {
                                @Override public DataVersion getDataVersion() {return dataVersion;}
                                @Override public long getDataVersionCounter() {return counter;}
                            };
                        }
                    };
        }
    }

    public interface EntityServiceConfig extends Receiver.SetInitialValues {
        EntityName getEntityName();
        ServiceName getPersistencyService();
    }
}
