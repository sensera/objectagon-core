package org.objectagon.core.object.service;


import org.objectagon.core.msg.Message;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public abstract class ObjectService<A extends Service.ServiceName, I extends Identity, D extends Data<I,StandardVersion>> extends EntityService<A, I, D, EntityService.EntityServiceWorker> {

    public ObjectService(ReceiverCtrl receiverCtrl, ServiceName name) {
        super(receiverCtrl, name);
    }

    @Override
    protected DataVersion<I, StandardVersion> createInitialDataFromValues(I identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0, StandardVersion::create);
    }

}
