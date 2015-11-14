package org.objectagon.core.domain;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-11-14.
 */
public class DomainService extends AbstractService<Service.ServiceWorker> {

    public DomainService(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ServiceWorker createWorker(WorkerContext workerContext) {
        return new ServiceWorkerImpl(workerContext);
    }

}
