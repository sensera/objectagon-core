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
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.*;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceService extends AbstractService<PersistenceService.PersistenceServiceWorkerImpl, StandardAddress> implements PersistenceServiceActionInitializer {

    Map<Key,Message.Values> stored = new HashMap<>();

    @Override
    public void store(Identity identity, Version version, Message.Values values) {
        stored.put(new Key(identity, version), values);
    }

    @Override
    public Message.Values get(Identity identity, Version version) {
        return stored.get(new Key(identity, version));
    }

    @Override
    public void remove(Identity identity, Version version) {
        stored.remove(new Key(identity, version));
    }

    @Override
    public void all(Identity identity, AllVersions allVersions) {
        stored.keySet().stream()
                .filter(key -> key.identity.equals(identity))
                .forEach(key -> allVersions.valuesVersions(key.version, stored.get(key)));
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
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.CREATE),
                (initializer, context) -> new CreateDataAction((PersistenceServiceActionInitializer) initializer, (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.UPDATE),
                (initializer, context) -> new UpdateDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.GET),
                (initializer, context) -> new GetDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(PersistenceServiceProtocol.MessageName.REMOVE),
                (initializer, context) -> new RemoveDataAction( (PersistenceServiceActionInitializer) initializer,  (PersistenceServiceWorkerImpl) context)
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

    private static class CreateDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        Message.Values values;
        public CreateDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            values = context.getValue(StandardField.VALUES).asValues();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            initializer.store(identity, version, values);
            return Optional.empty();
        }
    }

    private static class UpdateDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        Message.Values values;
        public UpdateDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
            super(serviceActionCommands, serviceWorker);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            version = StandardVersion.create(context.getValue(Version.VERSION));
            values = context.getValue(StandardField.VALUES).asValues();
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            initializer.store(identity, version, values);
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
            Message.Values values = initializer.get(identity, version);
            return Optional.of(MessageValue.values(values));
        }
    }

    private static class RemoveDataAction extends StandardAction<PersistenceServiceActionInitializer, PersistenceServiceWorkerImpl> {
        Identity identity;
        Version version;
        public RemoveDataAction(PersistenceServiceActionInitializer serviceActionCommands, PersistenceServiceWorkerImpl serviceWorker) {
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
            initializer.remove(identity, version);
            return Optional.empty();
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
            initializer.all(identity, (version, values) -> {
                List<Message.Value> versionAndValues = new LinkedList<>();
                versionAndValues.add(version.asValue());
                versionAndValues.add(MessageValue.values(values));
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
