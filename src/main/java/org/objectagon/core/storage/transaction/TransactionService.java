package org.objectagon.core.storage.transaction;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionService extends AbstractService<TransactionService.TransactionServiceWorkerImpl, StandardAddress, Receiver.CreateNewAddressParams> {

    public TransactionService(StandardReceiverCtrl<Receiver.CreateNewAddressParams> receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected CreateNewAddressParams createNewAddressParams() {return null;}

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
