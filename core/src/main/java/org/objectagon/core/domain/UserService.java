package org.objectagon.core.domain;

import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.service.StandardServiceNameAddress;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardIdentity;

/**
 * Created by christian on 2015-11-01.
 */
public class UserService extends EntityService<Service.ServiceName, StandardIdentity, StandardData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("USER_SERVICE");

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
    protected ServiceName createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<ServiceName> initializer) {
        return StandardServiceNameAddress.name(NAME, serverId, timestamp, id);
    }

    @Override
    protected DataVersion<StandardIdentity, StandardVersion> createInitialDataFromValues(StandardIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0l), 0l, StandardVersion::new);
    }

    @Override
    public Entity.EntityConfig createEntityConfigForInitialization(DataVersion dataVersion, Long counter, MessageValueFieldUtil messageValueFieldUtil) {
        return new Entity.EntityConfig() {
            @Override
            public DataVersion getDataVersion() {
                return dataVersion;
            }

            @Override
            public long getDataVersionCounter() {
                return counter;
            }
        };
    }
}
