package org.objectagon.core.domain;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardIdentity;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-11-01.
 */
public class UserService extends EntityService<Service.ServiceName, StandardIdentity, StandardData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("USER_SERVICE");

    public UserService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, User.DATA_TYPE);
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

    @Override
    protected Server.Factory createEntityFactory() {return User::new;}

    @Override
    protected DataVersion<StandardIdentity, StandardVersion> createInitialDataFromValues(StandardIdentity identity, Message.Values initialParams, Transaction transaction) {
        return DataVersionImpl.create(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

}
