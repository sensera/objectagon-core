package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassService extends EntityService<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, EntityService.EntityServiceWorker>  {

    public InstanceClassService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected Server.Factory createEntityFactory() {
        return InstanceClassImpl::new;
    }

    @Override
    protected DataVersion createInitialDataFromValues(InstanceClass.InstanceClassIdentity identity, Version version, Iterable<Message.Value> values) {
        return new DataVersionImpl<>(identity, version);
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected InstanceClass.InstanceClassIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new InstanceClassIdentityImpl(serverId, timestamp, id);
    }
}
