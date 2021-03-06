package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataRevisionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-07.
 */
public class InstanceService extends EntityService<Service.ServiceName, Instance.InstanceIdentity, Instance.InstanceData, EntityService.EntityServiceWorker> {
    public static ServiceName NAME = StandardServiceName.name("INSTANCE_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, InstanceService::new);
    }

    public InstanceService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, Instance.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() { return InstanceImpl::new; }

    @Override
    protected DataRevision<Instance.InstanceIdentity, StandardVersion> createInitialDataFromValues(Instance.InstanceIdentity identity, Message.Values initialParams, Transaction transaction) {
        return DataRevisionImpl.create(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of((InstanceProtocol.ConfigInstance) () -> messageValueFieldUtil.getValueByFieldOption(InstanceClass.INSTANCE_CLASS_IDENTITY)
                .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(InstanceClass.INSTANCE_CLASS_IDENTITY,"UNKNOWN")))
                .asAddress()
        );
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
