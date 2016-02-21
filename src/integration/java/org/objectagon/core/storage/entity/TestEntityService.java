package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardIdentity;

/**
 * Created by christian on 2016-01-09.
 */
public class TestEntityService extends EntityService<Identity, StandardData, EntityService.EntityServiceWorker> {

    public static Name TEST_ENTITY_SERVICE_NAME = new StandardName("test.entity.service");
    public static Address TEST_ENTITY_SERVICE_ADDRESS = new NamedAddress(TEST_ENTITY_SERVICE_NAME);

    public static void registerAtServer(Server server) {
        server.registerFactory(TEST_ENTITY_SERVICE_NAME, TestEntityService::new);
    }

    public TestEntityService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected Server.Factory createEntityFactory() {return TestEntity::new;}

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }
    @Override
    protected StandardData createInitialDataFromValues(Identity identity, Version version, Iterable<Message.Value> values) {
        return StandardData.create(identity, version).setValues(() -> values).create();
    }

    @Override
    protected Identity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new StandardIdentity(serverId, timestamp, id);
    }
}
