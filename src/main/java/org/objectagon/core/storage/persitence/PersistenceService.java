package org.objectagon.core.storage.persitence;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceService extends AbstractService<PersistenceService.PersistenceServiceWorkerImpl, StandardAddress, Receiver.CreateNewAddressParams> {


    public PersistenceService(StandardReceiverCtrl<Receiver.CreateNewAddressParams> receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected CreateNewAddressParams createNewAddressParams() {return null;}

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
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
