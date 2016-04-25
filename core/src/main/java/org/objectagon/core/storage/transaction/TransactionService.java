package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.*;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.storage.transaction.data.TransactionDataImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionService extends AbstractService<TransactionService.TransactionServiceWorkerImpl, Service.ServiceName> {

    public static ServiceName NAME = StandardServiceName.name("TRANSACTION_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, TransactionService::new);
    }

    private Map<Transaction,TransactionManager> transactionManagers = new HashMap<>();

    public TransactionService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) -> StandardServiceNameAddress.name(NAME, serverId, timestamp, addressId));
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionServiceProtocol.MessageName.CREATE),
                (initializer, context) -> new CreateAction(this, (TransactionServiceWorkerImpl) context)
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionServiceProtocol.MessageName.EXTEND),
                (initializer, context) -> new CreateAction(this, (TransactionServiceWorkerImpl) context)
        );
    }

    @Override
    protected TransactionServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new TransactionServiceWorkerImpl(workerContext);
    }

    public class TransactionServiceWorkerImpl extends ServiceWorkerImpl {
        public TransactionServiceWorkerImpl(WorkerContext workerContext) {
            super(workerContext);
        }

        public PersistenceServiceProtocol.Send createPersistenceServiceProtocolSend(Name target) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,PersistenceServiceProtocol>createReceiver(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL)
                    .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
        }
    }

    private class CreateAction extends AsyncAction<TransactionService, TransactionServiceWorkerImpl> {
        TransactionDataImpl transactionData;
        TransactionManager transactionManager;
        Transaction extendsTransaction;

        public CreateAction(TransactionService initializer, TransactionServiceWorkerImpl context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            extendsTransaction = context.getValue(Transaction.EXTENDS).asAddress();
            return super.initialize();
        }

        @Override
        protected Task internalRun(TransactionServiceWorkerImpl actionContext) throws UserException {
            Configurations configurations = OneReceiverConfigurations.create(
                    TransactionManagerProtocol.TRANSACTION_MANAGER_CONFIG,
                    new TransactionManagerProtocol.TransactionManagerConfig() {
                        @Override
                        public TransactionManager.TransactionData getTransactionData(Transaction transaction) {
                            transactionData = TransactionDataImpl.create(transaction, StandardVersion.create(0L));
                            if (extendsTransaction!=null)
                                transactionData = transactionData.<TransactionManager.TransactionDataChange>change()
                                        .setExtendsTransaction(extendsTransaction)
                                        .create(transactionData.getVersion());
                            return transactionData;
                        }

                        @Override
                        public Optional<Transaction> extendsTransaction() {
                            return Optional.ofNullable(extendsTransaction);
                        }
            });
            transactionManager = context.createReceiver(TransactionManagerImpl.NAME, configurations);
            transactionManagers.put(transactionManager.getAddress(), transactionManager);
            setSuccessAction((messageName, values) -> context.replyWithParam(MessageValue.address(transactionManager.getAddress())));
            return context.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(transactionData);
        }
    }

}
