package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.RelationProtocol;
import org.objectagon.core.object.service.ObjectService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;

import java.util.Optional;

/**
 * Created by christian on 2016-03-07.
 */
public class RelationService extends ObjectService<Service.ServiceName, Relation.RelationIdentity, Relation.RelationData> {

    public static ServiceName NAME = StandardServiceName.name("RELATION_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, RelationService::new);
    }

    public RelationService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME);
    }

    @Override protected Server.Factory createEntityFactory() {
        return RelationImpl::new;
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of(new RelationProtocol.ConfigRelation() {
            @Override
            public RelationClass.RelationClassIdentity getRelationClassIdentity() {
                return messageValueFieldUtil.getValueByField(RelationClass.RELATION_CLASS_IDENTITY).asAddress();
            }

            @Override
            public Instance.InstanceIdentity getInstanceIdentity() {
                return messageValueFieldUtil.getValueByField(Instance.INSTANCE_IDENTITY).asAddress();
            }
        });
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
