package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.LazyInitializedConfigurations;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.objectagon.core.storage.Entity.ENTITY_CONFIG_NAME;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<A extends Service.ServiceName, I extends Identity, D extends Data, W extends EntityService.EntityServiceWorker> extends AbstractService<W,A> implements EntityServiceActionInitializer {

    public static Name ENTITY_SERVICE_CONFIG_NAME = StandardName.name("ENTITY_SERVICE_CONFIG_NAME");
    public static Name EXTRA_ADDRESS_CONFIG_NAME = StandardName.name("EXTRA_ADDRESS_CONFIG_NAME");

    private EntityName entityName;
    private ServiceName persistencyService;

    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();
    private ServiceName name;

    public EntityService(ReceiverCtrl receiverCtrl, ServiceName name) {
        super(receiverCtrl);
        this.name = name;
    }

    @Override
    protected A createAddress(Configurations... configurations) {
        return (A) FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, id) -> StandardServiceNameAddress.name(name, serverId, timestamp, id));
    }


    @Override
    public void configure(Configurations... configurations) {
        super.configure();
        EntityServiceConfig config = FindNamedConfiguration.finder(configurations).getConfigurationByName(ENTITY_SERVICE_CONFIG_NAME);
        entityName = config.getEntityName();
        persistencyService = config.getPersistencyService();
        getReceiverCtrl().registerFactory(entityName, createEntityFactory());
    }

    protected abstract Server.Factory createEntityFactory();

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.CREATE_ENTITY),
                (initializer, context) -> new CreateEntityAction<W, I, D>((EntityService.this), (W) context));
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EntityServiceProtocol.MessageName.GET_ENTITY),
                (initializer, context) -> new GetEntityAction<W, I, D>((EntityService.this), (W) context));
    }

    protected abstract DataVersion<I,StandardVersion> createInitialDataFromValues(I identity, Message.Values initialParams);


    @Override
    public void addCompletedListener(W serviceWorker) {

    }

    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) { return Optional.empty(); }

    public class EntityServiceWorker extends ServiceWorkerImpl {
        public EntityServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
        public EntityName getEntityName() {return entityName;}

        public DataVersion<I,StandardVersion> getInitializeEntityWithValues(I identity, Message.Values initialParams) {
            if (identity==null)
                return null;
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
            Configurations entityConfigurations = createInitializer(context, this);

            Entity<I,D> entity = context.createReceiver(context.getEntityName(), entityConfigurations);
            if (entity==null)
                throw new NullPointerException("entity is null!");

            setSuccessAction((messageName, values) -> context.replyWithParam(MessageValue.address(entity.getAddress())));

            return context.persistDataVersion(dataVersion);
        }

        private Configurations createInitializer(W context, final Consumer<DataVersion<I, StandardVersion>> dataVersionConsumer) {
            LazyInitializedConfigurations configurations = LazyInitializedConfigurations.create();

            MessageValueFieldUtil messageValueFieldUtil = MessageValueFieldUtil.create(context.getValues());

            configurations.add(EXTRA_ADDRESS_CONFIG_NAME, () -> initializer.extraAddressCreateConfiguration(messageValueFieldUtil).get());

            configurations.add(ENTITY_SERVICE_CONFIG_NAME, () -> new Entity.EntityConfig() {
                    @Override public <I extends Identity, V extends Version> DataVersion<I, V> getDataVersion(I identity) {
                        DataVersion dataVersion = context.getInitializeEntityWithValues(identity, initialParams);
                        dataVersionConsumer.accept(dataVersion);
                        return dataVersion;
                    }
                    @Override public long getDataVersionCounter() {return 0;}
            });

            return configurations;
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
                Configurations entityConfigurations = createInitializer(dataVersion, counter, context);
                Entity<I, D> entity = context.createReceiver(context.getEntityName(), entityConfigurations);
                if (entity == null)
                    throw new NullPointerException("entity is null!");
                context.replyWithParam(MessageValue.address(entity.getAddress()));
            });

            return latestDataVersionFromPeristence;
        }

        private Configurations createInitializer(final DataVersion dataVersion, final Long counter, final W worker) {
            return OneReceiverConfigurations.create(ENTITY_CONFIG_NAME, new Entity.EntityConfig() {
                @Override public <I extends Identity, V extends Version> DataVersion<I, V> getDataVersion(I identity) {return dataVersion;}
                @Override public long getDataVersionCounter() {return counter;}
            });
        }
    }

    public interface EntityServiceConfig extends NamedConfiguration {
        EntityName getEntityName();
        ServiceName getPersistencyService();
    }
}
