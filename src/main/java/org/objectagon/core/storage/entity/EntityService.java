package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.Identity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityService<I extends Identity, D extends Data, E extends EntityService.EntityServiceWorker> extends AbstractService<E> {

    private Map<I, Entity<I,D>> identityEntityMap = new HashMap<I, Entity<I,D>>();

    public EntityService(BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    protected Entity<I,D> internalCreateEntity(EntityServiceWorker serviceWorker) {

    }

    protected void createEntity(EntityServiceWorker serviceWorker) {
        Entity<I,D> entity = internalCreateEntity(serviceWorker);

        //serviceWorker.getValue(EntityServiceProtocol.FieldName.)
    }

    @Override
    protected E createWorker(WorkerContext workerContext) {
        return (E) new EntityServiceWorker(workerContext);
    }

    @Override
    public void addCompletedListener(E serviceWorker) {

    }

    @Override
    public StartServiceTask createStartServiceTask(E serviceWorker) {
        return null;
    }

    @Override
    public StopServiceTask createStopServiceTask(E serviceWorker) {
        return null;
    }

    @Override
    protected void handle(E serviceWorker) {
        if (serviceWorker.messageHasName(EntityServiceProtocol.MessageName.CREATE)) {
            createEntity(serviceWorker);
            serviceWorker.setHandled();
        } else
            super.handle(serviceWorker);
    }


    protected class EntityServiceWorker extends ServiceWorkerImpl {
        public EntityServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

//    private class LocalEntityCtrl
//
//    protected abstract class LocalEntityCtrl implements Entity.EntityCtrl<I> {
//
//
//        public I createNewAddress(Receiver receiver) {
//            return null;
//        }
//
//        public void transport(Envelope envelope) {
//
//        }
//    }
}
