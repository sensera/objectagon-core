package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;
import java.util.stream.Stream;

import static org.objectagon.core.storage.TransactionManagerProtocol.TRANSACTION_MANAGER_CONFIG;

/**
 * Created by christian on 2016-03-29.
 */
public class TransactionManagerImpl extends StandardReceiverImpl<Transaction, TransactionManagerImpl.TransactionManagerWorker> implements Reactor.ActionInitializer, TransactionManager {

    public static Service.ServiceName NAME = StandardServiceName.name("TRANSACTION_MANAGER");

    private enum TransactionTasks implements Task.TaskName { RemoveAllItemsAndSave }

    public static void registerAt(Server server) {
        server.registerFactory(NAME, TransactionManagerImpl::new);
    }

    private TransactionManager.TransactionData transactionData;

    //@Override protected boolean logLevelCheck(WorkerContextLogKind logKind) {return true;}

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
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        TransactionManagerProtocol.TransactionManagerConfig transactionManagerConfig =  finder.getConfigurationByName(TransactionManagerProtocol.TRANSACTION_MANAGER_CONFIG);
        return finder.createConfiguredAddress((serverId, timestamp, addressId) ->
            transactionManagerConfig.extendsTransaction()
                    .map(transaction -> StandardTransaction.extend(serverId, timestamp, addressId, transaction))
                    .orElse(StandardTransaction.create(serverId, timestamp, addressId))
        );
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        reactorBuilder
                .add(
                    patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.ADD_ENTITY_TO),
                    (initializer, context) -> new AddEntityToAction(this, (TransactionManagerWorker) context)
                ).add(
                    patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.COMMIT),
                    (initializer, context) -> new CommitAction(this, (TransactionManagerWorker) context)
                ).add(
                    patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.EXTEND),
                    (initializer, context) -> new ExtendAction(this, (TransactionManagerWorker) context)
                ).add(
                    patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.ASSIGN),
                    (initializer, context) -> new AssignAction(this, (TransactionManagerWorker) context)
                ).add(
                    patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionManagerProtocol.MessageName.TARGETS),
                    (initializer, context) -> new TargetsAction(this, (TransactionManagerWorker) context)
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
        TransactionServiceProtocol.Internal createTransactionServiceProtocolInternal(Name target) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,TransactionServiceProtocol>createReceiver(TransactionServiceProtocol.TRANSACTION_SERVICE_PROTOCOL)
                    .createInternal(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
        }

        Task persist(TransactionManager.TransactionData newTransactionData) {
            return createPersistenceServiceProtocolSend(PersistenceService.NAME)
                    .pushData(newTransactionData);
        }
    }

    private class AddEntityToAction extends AsyncAction<TransactionManagerImpl, TransactionManagerWorker> {
        Identity identity;

        public AddEntityToAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            identity = context.getValue(StandardField.ADDRESS).asAddress();
            return super.initialize();
        }

        @Override
        protected Task internalRun(TransactionManagerWorker actionContext) throws UserException {
            context.trace("AddEntityToAction.internalRun Add to transaction",MessageValue.address(identity), MessageValue.address(getAddress()));
            System.out.println("AddEntityToAction.internalRun ["+getAddress()+"] "+identity);
            TransactionManager.TransactionDataChange change = transactionData.change();
            change.add(identity);
            TransactionManager.TransactionData newTransactionData = change.create(transactionData.getVersion().nextVersion());
            transactionData = newTransactionData;
            return actionContext.persist(newTransactionData); // TODO what if persitance failes. Use lock instead?
        }
    }

    private class CommitAction extends AsyncAction<TransactionManagerImpl, TransactionManagerWorker> {

        public CommitAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            if (transactionData.getIdentities().findAny().isPresent())
                return super.initialize();
            context.trace("CommitAction.initialize NOTHING TO COMMIT!");
            context.replyOk();
            return false;
        }

        @Override
        protected Task internalRun(TransactionManagerWorker actionContext) throws UserException {
            actionContext.trace("Commit "+transactionData.countIdentities()+" items");
            Stream<Identity> identities = transactionData.getIdentities(); // TODO implement EntityProtocol and registerAt
            TaskBuilder taskBuilder = actionContext.getTaskBuilder();
            taskBuilder.addHeader(MessageValue.address(Transaction.TRANSACTION, getAddress()));
            TaskBuilder.SequenceBuilder sequence = taskBuilder.sequence(TransactionManagerProtocol.MessageName.COMMIT);
            AddressList<Address> addressList = AddressList.createFromSteam(identities.map(identity -> identity));
            addressList.stream().forEach(address -> {
                sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, address, send -> send.lock(getAddress()));
                sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, address, EntityProtocol.Send::commit);
                sequence.<EntityProtocol.Send>protocol(EntityProtocol.ENTITY_PROTOCOL, address, send -> send.unlock(getAddress()));
            });
/*
            sequence.action(TransactionTasks.RemoveAllItemsAndSave, () -> {
                TransactionManager.TransactionDataChange change = transactionData.change();
                transactionData.getIdentities().forEach(change::remove);
                actionContext.persist(change.create(transactionData.getVersion().nextVersion()));
            });
*/
            return sequence.create();
        }
    }

    private class ExtendAction extends AsyncAction<TransactionManagerImpl, TransactionManagerWorker> {

        public ExtendAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        protected Task internalRun(TransactionManagerWorker actionContext) throws UserException {
            return actionContext.createTransactionServiceProtocolInternal(TransactionService.NAME).extend(getAddress());
        }
    }

    private class AssignAction extends StandardAction<TransactionManagerImpl, TransactionManagerWorker> {

        public AssignAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            return Optional.of(MessageValue.address(getAddress()));
        }
    }

    private class TargetsAction extends StandardAction<TransactionManagerImpl, TransactionManagerWorker> {

        public TargetsAction(TransactionManagerImpl initializer, TransactionManagerWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            return super.initialize();
        }

        @Override
        protected Optional<Message.Value> internalRun() throws UserException {
            return Optional.of(MessageValue.values(transactionData.getIdentities().map(MessageValue::address)));
        }
    }
}
