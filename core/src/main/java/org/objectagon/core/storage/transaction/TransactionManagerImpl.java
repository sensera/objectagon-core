package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardReceiverImpl;
import org.objectagon.core.msg.receiver.StandardWorkerImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.stream.Stream;

import static org.objectagon.core.storage.TransactionManagerProtocol.TRANSACTION_MANAGER_CONFIG;

/**
 * Created by christian on 2016-03-29.
 */
public class TransactionManagerImpl extends StandardReceiverImpl<Transaction, TransactionManagerImpl.TransactionManagerWorker> implements Reactor.ActionInitializer, TransactionManager {

    public static Service.ServiceName NAME = StandardServiceName.name("TRANSACTION_MANAGER");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, TransactionManagerImpl::new);
    }

    private TransactionManager.TransactionData transactionData;

    public TransactionManagerImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        TransactionManagerProtocol.TransactionManagerConfig config = FindNamedConfiguration.finder(configurations).getConfigurationByName(TRANSACTION_MANAGER_CONFIG);
        transactionData = config.getTransactionData(getAddress());
    }

    @Override
    protected Transaction createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardTransaction::new);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.ADD_ENTITY_TO),
                (initializer, context) -> new AddEntityToAction(this, (TransactionManagerWorker) context)
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.COMMIT),
                (initializer, context) -> new CommitAction(this, (TransactionManagerWorker) context)
                );
    }

    @Override
    protected TransactionManagerWorker createWorker(WorkerContext workerContext) {
        return new TransactionManagerWorker(workerContext);
    }

    class TransactionManagerWorker extends StandardWorkerImpl {
        TransactionManagerWorker(WorkerContext workerContext) {
            super(workerContext);
        }
        PersistenceServiceProtocol.Send createPersistenceServiceProtocolSend(Name target) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,PersistenceServiceProtocol>createReceiver(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL)
                    .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
        }
        EntityProtocol.Send createEntityProtocolSend(Identity identity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, EntityProtocol>createReceiver(EntityProtocol.ENTITY_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(identity));
        }
    }

    private class AddEntityToAction extends AsyncAction<TransactionManagerImpl, TransactionManagerWorker> {
        Identity identity;

        public AddEntityToAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(Identity.IDENTITY).asAddress();
            return super.initialize();
        }

        @Override
        protected Task internalRun(TransactionManagerWorker actionContext) throws UserException {
            TransactionManager.TransactionDataChange change = transactionData.change();
            change.add(identity);
            TransactionManager.TransactionData newTransactionData = change.create(transactionData.getVersion().nextVersion());
            return actionContext.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(newTransactionData);
        }
    }

    private class CommitAction extends AsyncAction<TransactionManagerImpl, TransactionManagerWorker> {

        public CommitAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            return super.initialize();
        }

        @Override
        protected Task internalRun(TransactionManagerWorker actionContext) throws UserException {
            Stream<Identity> identities = transactionData.getIdentities(); // TODO implement EntityProtocol and registerAt
            TaskBuilder taskBuilder = actionContext.getTaskBuilder();
            TaskBuilder.SequenceBuilder sequence = taskBuilder.sequence(TransactionManagerProtocol.MessageName.COMMIT);
            AddressList addressList = AddressList.createFromSteam(identities);
            sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, addressList, send -> send.lock(getAddress()));
            sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, addressList, EntityProtocol.Send::commit);
            sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, addressList, send -> send.unlock(getAddress()));
            return sequence.create();
        }
    }
}
