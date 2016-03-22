package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;

/**
 * Created by christian on 2016-03-07.
 */
public class FieldValueService extends EntityService<Service.ServiceName, FieldValue.FieldValueIdentity, FieldValue.FieldValueData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("FIELD_VALUE_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, FieldValueService::new);
    }

    public FieldValueService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected Server.Factory createEntityFactory() {return FieldValueImpl::new;}

    @Override
    protected DataVersion<FieldValue.FieldValueIdentity, StandardVersion> createInitialDataFromValues(FieldValue.FieldValueIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }
}
