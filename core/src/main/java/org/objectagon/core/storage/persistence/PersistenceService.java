package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.*;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceService extends AbstractService<PersistenceService.PersistenceServiceWorkerImpl, Service.ServiceName> implements PersistenceServiceActionInitializer {

    public static ServiceName NAME = StandardServiceName.name("PERSISTENCE_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, PersistenceService::new);
    }

    Map<Identity,Data> latestVersion = new HashMap<>();
    Map<Key,Data> datas = new HashMap<>();
    Map<Key,DataRevision> dataVersions = new HashMap<>();
    Map<Key,TransactionManager.TransactionData> dataTransactions = new HashMap<>();

    @Override
    public void pushData(Data data) {
        datas.put(new Key(data), data);
    }

    @Override
    public Optional<Data> getData(Identity identity, Version version) {
        return Optional.ofNullable(datas.get(new Key(identity, version)));
    }

    @Override
    public Optional<DataRevision> getDataVersion(Identity identity, Version version) {
        return Optional.ofNullable(dataVersions.get(new Key(identity, version)));
    }

    @Override
    public void all(Identity identity, Consumer<Data> allVersions) {
        datas.keySet().stream()
                .filter(identity::equals)
                .map(datas::get)
                .forEach(allVersions);
    }

    @Override
    public void all(Data.Type type, Consumer<Data> result) {
        datas.values().stream()
                .filter(data -> data.getDataType().equals(type))
                .forEach(result);
    }

    @Override
    public void putTransactionData(Identity identity, Version version, TransactionManager.TransactionData transactionData) {
        dataTransactions.put(new Key(identity, version), transactionData);
    }

    public PersistenceService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected Service.ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) ->
                StandardServiceNameAddress.name(NAME, serverId, timestamp, addressId)
        );
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.PUSH_DATA),
                (initializer, context) -> new PushDataAction((PersistenceServiceActionInitializer) initializer, (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.GET_DATA),
                (initializer, context) -> new GetDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.GET_LATEST_DATA),
                (initializer, context) -> new GetDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.ALL_BY_ID),
                (initializer, context) -> new AllByIdDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.ALL_BY_TYPE),
                (initializer, context) -> new AllByTypeDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
    }

    @Override
    protected PersistenceServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new PersistenceServiceWorkerImpl(workerContext);
    }

    class PersistenceServiceWorkerImpl extends ServiceWorkerImpl {
        public PersistenceServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }
    }

    private static class PushDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Iterable<Data> data;
        public PushDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            Message.Values values = context.getValue(StandardField.VALUES).asValues();
            data = StreamSupport.stream(values.values().spliterator(), false)
                    .map(value -> value.<Data>getOptionalValue().get())
                    .collect(Collectors.toList());
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            data.forEach(data -> initializer.pushData(data));
            return Optional.empty();
        }
    }

    private static class GetDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Data.Type dataType;
        Identity identity;
        Version version;
        public GetDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            dataType = context.getValue(Data.DATA).<Data.Type>getOptionalValue().get();
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION)); //TODO Optional
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            Optional<Data> data = initializer.getData(identity, version); //TODO check if version is optional present. Do get without version
            return data.flatMap(data1 -> Optional.of(DataMessageValue.data(data1)));
        }
    }

    private static class AllByIdDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        public AllByIdDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            List<Message.Value> collectedVersionValues = new LinkedList<>();
            initializer.all(identity, (data) -> {
                List<Message.Value> versionAndValues = new LinkedList<>();
                versionAndValues.add(DataMessageValue.data(data));
                collectedVersionValues.add(MessageValue.values(versionAndValues));
            });
            return Optional.of(MessageValue.values(collectedVersionValues));
        }
    }

    private static class AllByTypeDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Data.Type dataType;
        public AllByTypeDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            dataType = context.getValue(StandardField.NAME).asName();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            List<Message.Value> collectedVersionValues = new LinkedList<>();
            initializer.all(dataType, (data) -> {
                List<Message.Value> versionAndValues = new LinkedList<>();
                versionAndValues.add(DataMessageValue.data(data));
                collectedVersionValues.add(MessageValue.values(versionAndValues));
            });
            return Optional.of(MessageValue.values(collectedVersionValues));
        }
    }

    static class Key {
        Identity identity;
        Version version;

        public Key(Identity identity, Version version) {
            this.identity = identity;
            this.version = version;
        }

        public Key(Data data) {
            this(data.getIdentity(), data.getVersion());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(identity, key.identity) &&
                    Objects.equals(version, key.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identity, version);
        }
    }



}
