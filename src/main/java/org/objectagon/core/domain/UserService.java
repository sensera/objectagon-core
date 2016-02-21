package org.objectagon.core.domain;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardIdentity;

/**
 * Created by christian on 2015-11-01.
 */
public class UserService extends EntityService<Identity, StandardData, EntityService.EntityServiceWorker> {

    public UserService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected Server.Factory createEntityFactory() {return User::new;}

    @Override
    protected Identity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new StandardIdentity(serverId, timestamp, id);
    }

    @Override
    protected StandardData createInitialDataFromValues(Identity identity, Version version, Iterable<Message.Value> values) {
        return StandardData.create(identity, version).setValues(() -> values).create();
    }
}
