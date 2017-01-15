package org.objectagon.core.object.relationclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.object.service.ObjectService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;

import java.util.Optional;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassService extends ObjectService<Service.ServiceName, RelationClass.RelationClassIdentity, RelationClass.RelationClassData> {

    public static ServiceName NAME = StandardServiceName.name("RELATION_CLASS_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, RelationClassService::new);
    }

    public RelationClassService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, RelationClass.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {
        return RelationClassImpl::new;
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of(new RelationClassProtocol.ConfigRelationClass() {
            @Override
            public InstanceClass.InstanceClassIdentity getInstanceClassIdentity(RelationClass.RelationDirection relationDirection) {
                return RelationDirectionUtil.create(relationDirection).getInstanceClassIdentity(messageValueFieldUtil);
            }

            @Override
            public RelationClass.RelationType getRelationType() {
                return RelationClass.RelationType.valueOf(messageValueFieldUtil.getValueByField(RelationClass.RELATION_TYPE).asText());
            }
        });
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
