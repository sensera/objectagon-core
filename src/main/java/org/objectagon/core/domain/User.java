package org.objectagon.core.domain;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;

/**
 * Created by christian on 2015-11-01.
 */
public class User extends EntityImpl<Identity, UserData, Version, EntityWorker> {

    public User(EntityCtrl<Identity> entityCtrl, UserData data) {
        super(entityCtrl, data);
    }

    @Override
    protected Version createVersionFromValue(Message.Value value) {
        return null;
    }

    @Override
    protected void handle(EntityWorker worker) {

    }

    @Override
    protected EntityWorker createWorker(WorkerContext workerContext) {
        return null;
    }
}
