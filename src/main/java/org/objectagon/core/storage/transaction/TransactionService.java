package org.objectagon.core.storage.transaction;

import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionService extends AbstractService<TransactionService.TransactionServiceWorkerImpl> {

    public TransactionService(BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void handle(TransactionServiceWorkerImpl serviceWorker) {
        super.handle(serviceWorker);
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
