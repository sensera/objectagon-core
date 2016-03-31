package org.objectagon.core.object.relationclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.service.ObjectService;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Entity;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassService extends ObjectService<Service.ServiceName, RelationClass.RelationClassIdentity, RelationClass.RelationClassData> {

    public static ServiceName NAME = StandardServiceName.name("RELATION_CLASS_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, RelationClassService::new);
    }

    public RelationClassService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected Server.Factory createEntityFactory() {
        return RelationClassImpl::new;
    }

    @Override
    public Entity.EntityConfig createEntityConfigForInitialization(DataVersion dataVersion, Long counter, MessageValueFieldUtil messageValueFieldUtil) {
        return new RelationClassProtocol.ConfigRelationClass() {
            @Override
            public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
                return messageValueFieldUtil.getValueByField(InstanceClass.INSTANCE_CLASS_IDENTITY).asAddress();
            }

            @Override
            public RelationClass.RelationType getRelationType() {
                return RelationClass.RelationType.valueOf(messageValueFieldUtil.getValueByField(RelationClass.RELATION_TYPE).asText());
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

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }
}
