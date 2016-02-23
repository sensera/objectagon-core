package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.object.ObjectVersion;
import org.objectagon.core.storage.*;
import org.objectagon.core.task.*;

import static org.objectagon.core.object.ObjectVersion.objectVersion;

/**
 * Created by christian on 2015-11-01.
 */
public class EntityWorkerImpl extends BasicWorkerImpl implements EntityWorker {

    public EntityWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }

//    public PersistenceServiceProtocol createPersistenceServiceProtocol() {
//        return getWorkerContext().createSession(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, getWorkerContext().createReplyToSenderComposer());
//    }

    @Override
    public TaskBuilder getTaskBuilder() {
        throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED);
    }

    public ObjectVersion getVersion() {
        return objectVersion(getValue(EntityProtocol.FieldName.VERSION));
    }

}
