package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-06.
 */
public class FieldService extends EntityService<Service.ServiceName, Field.FieldIdentity, Field.FieldData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("FIELD_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, FieldService::new);
    }

    public FieldService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected Server.Factory createEntityFactory() {return FieldImpl::new;}

    @Override
    protected DataVersion<Field.FieldIdentity, StandardVersion> createInitialDataFromValues(Field.FieldIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override
    public Entity.EntityConfig createEntityConfigForInitialization(DataVersion dataVersion, Long counter, MessageValueFieldUtil messageValueFieldUtil) {
        return new FieldProtocol.ConfigField(){
            @Override
            public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
                return messageValueFieldUtil.getValueByField(InstanceClass.INSTANCE_CLASS_IDENTITY).asAddress();
            }

            @Override
            public DataVersion getDataVersion() {
                return dataVersion;
            }

            @Override
            public long getDataVersionCounter() {
                return counter;
            }
        };
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected Service.ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<Service.ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }

}
