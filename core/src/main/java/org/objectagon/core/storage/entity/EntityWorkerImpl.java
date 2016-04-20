package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.FieldValueProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.event.EventServiceProtocol;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Transaction;


/**
 * Created by christian on 2015-11-01.
 */
public class EntityWorkerImpl extends BasicWorkerImpl implements EntityWorker {

    public EntityWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }


    @Override
    public Transaction currentTransaction() {
        return getWorkerContext().getHeader(Transaction.TRANSACTION).asAddress();
    }

    public EntityServiceProtocol.Send createEntityServiceProtocolSend(Service.ServiceName serviceAddress, Name target) {
        return getWorkerContext()
                .<Protocol.ProtocolAddress,EntityServiceProtocol>createReceiver(
                        EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL)
                .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
    }

    public PersistenceServiceProtocol.Send createPersistenceServiceProtocolSend(Name target) {
        return getWorkerContext()
                .<Protocol.ProtocolAddress,PersistenceServiceProtocol>createReceiver(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL)
                .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE, target));
    }

    public EventServiceProtocol.Send createEventServiceProtocolSend(Service.ServiceName serviceAddress) {
        if (serviceAddress==null) throw new NullPointerException("ServiceAddress is null!");
        return getWorkerContext()
                .<Protocol.ProtocolAddress,EventServiceProtocol>createReceiver(
                        EventServiceProtocol.EVENT_SERVICE_PROTOCOL)
                .createSend(() -> getWorkerContext().createForwardComposer(serviceAddress));
    }

    public FieldValueProtocol.Send createFieldValueProtocolSend(FieldValue.FieldValueIdentity fieldValueIdentity) {
        return getWorkerContext()
                .<Protocol.ProtocolAddress,FieldValueProtocol>createReceiver(FieldValueProtocol.FIELD_VALUE_PROTOCOL)
                .createSend(() -> getWorkerContext().createTargetComposer(fieldValueIdentity));
    }
}
