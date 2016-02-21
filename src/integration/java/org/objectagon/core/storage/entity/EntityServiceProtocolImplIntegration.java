package org.objectagon.core.storage.entity;

import org.objectagon.core.IntegrationTests;
import org.objectagon.core.Suite;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardIdentity;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Collections;

/**
 * Created by christian on 2015-12-27.
 */
public class EntityServiceProtocolImplIntegration implements Suite {

    TestEntityService testEntityService;
    Name testName = StandardName.name("testName");
    Address persistency = StandardAddress.standard(null, 20, 10);
    EntityName entityName = EntityName.create("EntityName");

    public static void register(RegisterSuite registerSuite) {
        registerSuite.add(new EntityServiceProtocolImplIntegration());
    }

    public void setup(Setup setup) {
        setup.registerAtServer(TestEntityService::registerAtServer);
        setup.registerAtServer(TestEntity::registerAtServer);

        setup.registerAtServer(server -> {
            EntityServiceProtocolImpl.registerAtServer(server);
            Receiver.Initializer<Identity> initializer = new Receiver.Initializer<Identity>() {
                @Override
                public <C extends Receiver.SetInitialValues> C initialize(Identity identity) {
                    return (C) new MyEntityServiceConfig(entityName);
                }
            };
            testEntityService = server.createReceiver(TestEntityService.TEST_ENTITY_SERVICE_NAME, initializer);
        });
    }

    public void createTests(IntegrationTests tests) {
        Version version = new StandardVersion(0l);
        IntegrationTests.IntegrationTestEntityAction<EntityServiceProtocol> testCreate = (testEntity, commander, composer, protocol) -> protocol.createSend(() -> composer).create(version);
        tests.registerTest("Create",
                EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL,
                testEntityService.getAddress(),
                testCreate
        );

/*
        IntegrationTests.IntegrationTestEntityAction<EntityServiceProtocol.Session> testGet = (testEntity, commander, session) -> session.get();
        tests.registerTest("Get",
                EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL,
                testEntityService.getAddress(),
                testCreate
        );
*/

    }

    private class MyEntityServiceConfig implements EntityService.EntityServiceConfig {
        private EntityName name;

        public MyEntityServiceConfig(EntityName name) {
            this.name = name;
        }

        @Override
        public EntityName getEntityName() {
            return name;
        }
    }

}
