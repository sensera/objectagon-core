package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassService extends EntityService<Service.ServiceName, InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, EntityService.EntityServiceWorker>  {

    public static ServiceName NAME = StandardServiceName.name("INSTANCE_CLASS_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, InstanceClassService::new);
    }

    public InstanceClassService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, InstanceClass.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return InstanceClassImpl::new;}
    @Override protected DataVersion<InstanceClass.InstanceClassIdentity, StandardVersion> createInitialDataFromValues(InstanceClass.InstanceClassIdentity identity, Message.Values initialParams, Transaction transaction) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {return new InstanceClassServiceWorker(workerContext);}

    private class InstanceClassServiceWorker extends EntityServiceWorker {
        public InstanceClassServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Optional<Task> find(Name name) {
            return Optional.of(nameSearch(name));
        }
    }


}
