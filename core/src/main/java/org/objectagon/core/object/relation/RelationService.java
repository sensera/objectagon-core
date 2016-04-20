package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
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
        super(receiverCtrl, NAME, Relation.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {
        return RelationImpl::new;
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of(new RelationProtocol.ConfigRelation() {
            @Override
            public RelationClass.RelationClassIdentity getRelationClassIdentity() {
                return messageValueFieldUtil.getValueByFieldOption(RelationClass.RELATION_CLASS_IDENTITY)
                        .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(RelationClass.RELATION_CLASS_IDENTITY,"UNKNOWN")))
                        .asAddress();
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
