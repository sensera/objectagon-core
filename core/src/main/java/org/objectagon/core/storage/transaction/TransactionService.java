package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.*;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionService extends AbstractService<TransactionService.TransactionServiceWorkerImpl, Service.ServiceName> {

    public static ServiceName NAME = StandardServiceName.name("TRANSACTION_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, TransactionService::new);
    }

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
    }

    @Override
    protected TransactionServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new TransactionServiceWorkerImpl(workerContext);
    }

    public class TransactionServiceWorkerImpl extends ServiceWorkerImpl {
        public TransactionServiceWorkerImpl(WorkerContext workerContext) {
            super(workerContext);
        }
    }
}
