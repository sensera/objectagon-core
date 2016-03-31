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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionService extends AbstractService<TransactionService.TransactionServiceWorkerImpl, Service.ServiceName> {

    public static ServiceName NAME = StandardServiceName.name("TRANSACTION_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, TransactionService::new);
    }

    Map<Transaction,TransactionManager> transactionManagers = new HashMap<>();

    public TransactionService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(TransactionServiceProtocol.MessageName.CREATE),
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
                    .<Protocol.ProtocolAddress,PersistenceServiceProtocol>createReceiver(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, null)
                    .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
        }
    }

    private class CreateAction extends AsyncAction<TransactionService, TransactionServiceWorkerImpl> {
        TransactionDataImpl transactionData;
        TransactionManager transactionManager;

        public CreateAction(TransactionService initializer, TransactionServiceWorkerImpl context) {
            super(initializer, context);
        }

        @Override
        protected Task internalRun(TransactionServiceWorkerImpl actionContext) throws UserException {
            transactionManager = context.createReceiver(TransactionManagerImpl.NAME, new Initializer<Transaction>() {
                @Override
                public <C extends SetInitialValues> C initialize(final Transaction address) {
                    transactionData = TransactionDataImpl.create(address, StandardVersion.create(0L));
                    return (C) (TransactionManagerProtocol.TransactionManagerConfig) () -> transactionData;
                }
            });
            transactionManagers.put(transactionManager.getAddress(), transactionManager);
            setSuccessAction((messageName, values) -> context.replyWithParam(MessageValue.address(transactionManager.getAddress())));
            return context.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushTransactionData(transactionData);
        }
    }
}
