package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassService extends EntityService<Service.ServiceName, InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, EntityService.EntityServiceWorker>  {

    public static ServiceName NAME = StandardServiceName.name("INSTANCE_CLASS_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, InstanceClassService::new);
    }

    public InstanceClassService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME);
    }

    @Override protected Server.Factory createEntityFactory() {return InstanceClassImpl::new;}
    @Override protected DataVersion<InstanceClass.InstanceClassIdentity, StandardVersion> createInitialDataFromValues(InstanceClass.InstanceClassIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {return new EntityServiceWorker(workerContext);}

}
