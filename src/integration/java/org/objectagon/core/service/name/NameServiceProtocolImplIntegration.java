package org.objectagon.core.service.name;

import org.objectagon.core.IntegrationTests;
import org.objectagon.core.Server;
import org.objectagon.core.Suite;
import org.objectagon.core.msg.*;
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

    public void setup(Server server) {
        nameService = server.createReceiver(NameServiceImpl.NAME_SERVICE_CTRL_NAME);
    }

    public void createTests(IntegrationTests tests) {
        tests.registerTest("RegisterName",
                        (testEntity, commander, session) -> session.registerName(nameService.getAddress(), testName)
        );
        tests.registerTest("LookupName",
                        (testEntity, commander, session) -> session.lookupAddressByName(testName),
                        "RegisterName"
        );
    }



}
