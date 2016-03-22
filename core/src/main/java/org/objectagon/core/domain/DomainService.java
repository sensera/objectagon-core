package org.objectagon.core.domain;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;

/**
 * Created by christian on 2015-11-14.
 */
public class DomainService extends AbstractService<Service.ServiceWorker, StandardAddress> {

    public DomainService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ServiceWorker createWorker(WorkerContext workerContext) {
        return new ServiceWorkerImpl(workerContext);
    }

    @Override
    protected StandardAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<StandardAddress> initializer) {
        return StandardAddress.standard(serverId, timestamp, id);
    }
}
