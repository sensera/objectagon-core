package org.objectagon.core.service.name;

import org.objectagon.core.IntegrationTests;
import org.objectagon.core.Suite;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.name.StandardName;

/**
 * Created by christian on 2015-12-27.
 */
public class NameServiceProtocolImplIntegration implements Suite {

    NameServiceImpl nameService;
    Name testName = StandardName.name("testName");

    public static void register(RegisterSuite registerSuite) {
        registerSuite.add(new NameServiceProtocolImplIntegration());
    }

    public void setup(Setup setup) {
        setup.registerAtServer(server -> {
            NameServiceProtocolImpl.registerAtServer(server);
            nameService = server.createReceiver(NameServiceImpl.NAME_SERVICE_ADDRESS, null);
        });
    }

    public void createTests(IntegrationTests tests) {
        IntegrationTests.IntegrationTestEntityAction<NameServiceProtocol> sessionIntegrationTestEntityAction = (testEntity, commander, composer,  protocol) -> protocol.createSend(() -> composer).registerName(nameService.getAddress(), testName);
        tests.registerTest("RegisterName",
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                NameServiceImpl.NAME_SERVICE_ADDRESS,
                sessionIntegrationTestEntityAction
        );

        IntegrationTests.IntegrationTestEntityAction<NameServiceProtocol> sessionIntegrationTestEntityAction1 = (testEntity, commander, composer, protocol) -> protocol.createSend(() -> composer).lookupAddressByName(testName);
        tests.registerTest("LookupName",
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                NameServiceImpl.NAME_SERVICE_ADDRESS,
                sessionIntegrationTestEntityAction1,
                "RegisterName"
        );
    }



}
