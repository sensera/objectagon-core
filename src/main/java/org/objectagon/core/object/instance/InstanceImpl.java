package org.objectagon.core.object.instance;

import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.object.ObjectVersion;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<InstanceIdentity,InstanceData,ObjectVersion,InstanceImpl.InstanceWorker> {

    public InstanceImpl(EntityCtrl<InstanceIdentity> entityCtrl, InstanceData data) {
        super(entityCtrl, data);
    }

    @Override
    protected void handle(BasicWorker worker) {

    }

    @Override
    public void receive(Envelope envelope) {
        super.receive(envelope);
    }

    @Override
    protected BasicWorker createWorker(WorkerContext workerContext) {
        return null;
    }

    @Override
    protected ObjectVersion createVersionFromValue(Message.Value value) {
        return null;
    }

    public static class InstanceWorker extends EntityWorkerImpl {
        public InstanceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

    }
}
