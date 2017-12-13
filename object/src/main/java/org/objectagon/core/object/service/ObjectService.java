package org.objectagon.core.object.service;


import org.objectagon.core.msg.Message;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataRevisionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public abstract class ObjectService<A extends Service.ServiceName, I extends Identity, D extends Data<I,StandardVersion>> extends EntityService<A, I, D, EntityService.EntityServiceWorker> {

    public ObjectService(ReceiverCtrl receiverCtrl, ServiceName name, Data.Type dataType) {
        super(receiverCtrl, name, dataType);
    }

    @Override
    protected DataRevision<I, StandardVersion> createInitialDataFromValues(I identity, Message.Values initialParams, Transaction transaction) {
        return DataRevisionImpl.create(identity, StandardVersion.create(0l), 0, StandardVersion::create);
    }

}
