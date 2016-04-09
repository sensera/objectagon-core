package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.entity.DataVersionImpl;
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
    protected DataVersion<Instance.InstanceIdentity, StandardVersion> createInitialDataFromValues(Instance.InstanceIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of((InstanceProtocol.ConfigInstance) () -> messageValueFieldUtil.getValueByField(InstanceClass.INSTANCE_CLASS_IDENTITY).asAddress());
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
