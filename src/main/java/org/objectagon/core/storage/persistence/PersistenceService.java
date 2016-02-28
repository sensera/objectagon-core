package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceService extends AbstractService<PersistenceService.PersistenceServiceWorkerImpl, StandardAddress> implements PersistenceServiceActionInitializer {

    Map<Key,Data> datas = new HashMap<>();
    Map<Key,DataVersion> dataVersions = new HashMap<>();

    @Override
    public void pushData(Identity identity, Version version, Data data) {
        datas.put(new Key(identity, version), data);
    }

    @Override
    public void pushDataVersion(Identity identity, Version version, DataVersion dataVersion) {
        dataVersions.put(new Key(identity, version), dataVersion);
    }

    @Override
    public Optional<Data> getData(Identity identity, Version version) {
        return Optional.ofNullable(datas.get(new Key(identity, version)));
    }

    @Override
    public Optional<DataVersion> getDataVersion(Identity identity, Version version) {
        return Optional.ofNullable(dataVersions.get(new Key(identity, version)));
    }

    @Override
    public void all(Identity identity, Consumer<Data> allVersions) {
        datas.keySet().stream()
                .filter(key -> key.identity.equals(identity))
                .forEach(key -> allVersions.accept(datas.get(key)));
    }

    public PersistenceService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return StandardAddress.standard(serverId, timestamp, id);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.PUSH_DATA),
                (initializer, context) -> new PushDataAction((PersistenceServiceActionInitializer) initializer, (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.PUSH_DATA_VERSION),
                (initializer, context) -> new PushDataVersionDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.GET_DATA),
                (initializer, context) -> new GetDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.GET_DATA_VERSION),
                (initializer, context) -> new GetDataVersionAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.ALL),
                (initializer, context) -> new AllDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
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
        Identity identity;
        Version version;
        Data data;
        public PushDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            data = context.getValue(Data.DATA).getValue();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            initializer.pushData(identity, version, data);
            return Optional.empty();
        }
    }

    private static class PushDataVersionDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        DataVersion dataVersion;
        public PushDataVersionDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            dataVersion = context.getValue(DataVersion.DATA_VERSIONS).getValue();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            initializer.pushDataVersion(identity, version, dataVersion);
            return Optional.empty();
        }
    }

    private static class GetDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        public GetDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            Optional<Data> data = initializer.getData(identity, version);
            return data.flatMap(data1 -> Optional.of(DataMessageValue.data(data1)));
        }
    }

    private static class GetDataVersionAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        public GetDataVersionAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            Optional<DataVersion> data = initializer.getDataVersion(identity, version);
            return data.flatMap(data1 -> Optional.of(DataMessageValue.data(data1)));
        }
    }

    private static class AllDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        public AllDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
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

    static class Key {
        Identity identity;
        Version version;

        public Key(Identity identity, Version version) {
            this.identity = identity;
            this.version = version;
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
