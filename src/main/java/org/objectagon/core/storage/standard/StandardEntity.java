package org.objectagon.core.storage.standard;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardEntity<D extends Data<StandardIdentity,StandardVersion>, R extends Receiver<StandardIdentity>> extends EntityImpl<StandardIdentity, D, StandardVersion, EntityWorker, R> {

    public StandardEntity(EntityCtrl<StandardIdentity, R> entityCtrl, D data) {
        super(entityCtrl, data);
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
