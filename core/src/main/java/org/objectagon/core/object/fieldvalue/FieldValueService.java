package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.FieldValueProtocol;
import org.objectagon.core.object.Instance;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

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
    public Entity.EntityConfig createEntityConfigForInitialization(DataVersion dataVersion, Long counter, MessageValueFieldUtil messageValueFieldUtil) {
        return new FieldValueProtocol.ConfigFieldValue(){
            @Override
            public Field.FieldIdentity getFieldIdentity() {
                return messageValueFieldUtil.getValueByField(Field.FIELD_IDENTITY).asAddress();
            }

            @Override
            public Instance.InstanceIdentity getInstanceIdentity() {
                return messageValueFieldUtil.getValueByField(Instance.INSTANCE_IDENTITY).asAddress();
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
    protected ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }
}
