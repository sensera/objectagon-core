package org.objectagon.core.storage.standard;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardEntity<D extends StandardData> extends EntityImpl<StandardIdentity, D, StandardVersion, EntityWorker> {


    public StandardEntity(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return StandardIdentity.standardIdentity(serverId, timestamp, id);
    }

    @Override
    protected StandardVersion createVersionFromValue(Message.Value value) {
        return new StandardVersion(value.asNumber());
    }

    @Override
    protected void handle(EntityWorker worker) {

    }

    @Override
    protected EntityWorker createWorker(WorkerContext workerContext) {return new EntityWorkerImpl(workerContext);}

}
