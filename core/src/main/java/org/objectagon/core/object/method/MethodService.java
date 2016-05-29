package org.objectagon.core.object.method;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.instanceclass.InstanceClassImpl;
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
public class MethodService extends EntityService<Service.ServiceName, Method.MethodIdentity, Method.MethodData, EntityService.EntityServiceWorker>  {

    public static ServiceName NAME = StandardServiceName.name("META_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, MethodService::new);
    }

    public MethodService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, Method.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return InstanceClassImpl::new;}
    @Override protected DataVersion<Method.MethodIdentity, StandardVersion> createInitialDataFromValues(Method.MethodIdentity identity, Message.Values initialParams, Transaction transaction) {
        return DataVersionImpl.create(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {return new MethodServiceWorker(workerContext);}

    private class MethodServiceWorker extends EntityServiceWorker {
        public MethodServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Optional<Task> find(Name name) {
            return Optional.of(nameSearch(name));
        }
    }


}
