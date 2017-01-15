package org.objectagon.core.object.meta;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.object.Meta;
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
public class MetaService extends EntityService<Service.ServiceName, Meta.MetaIdentity, Meta.MetaData, EntityService.EntityServiceWorker>  {

    public static ServiceName NAME = StandardServiceName.name("META_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, MetaService::new);
    }

    public MetaService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, Meta.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return MetaImpl::new;}
    @Override protected DataVersion<Meta.MetaIdentity, StandardVersion> createInitialDataFromValues(Meta.MetaIdentity identity, Message.Values initialParams, Transaction transaction) {
        return DataVersionImpl.create(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {return new MetaServiceWorker(workerContext);}

    private class MetaServiceWorker extends EntityServiceWorker {
        public MetaServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Optional<Task> find(Name name) {
            return Optional.of(nameSearch(name));
        }
    }


}
