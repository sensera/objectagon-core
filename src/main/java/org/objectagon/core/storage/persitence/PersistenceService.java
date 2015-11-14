package org.objectagon.core.storage.persitence;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceService extends AbstractService<PersistenceService.PersistenceServiceWorkerImpl> {

    public PersistenceService(BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void handle(PersistenceServiceWorkerImpl serviceWorker) {
        super.handle(serviceWorker);
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

}
