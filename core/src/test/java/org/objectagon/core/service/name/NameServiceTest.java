package org.objectagon.core.service.name;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNameValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.service.AbstractServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by christian on 2016-02-16.
 */
public class NameServiceTest extends AbstractServiceTest<NameServiceImpl> {

    private final Name testName = StandardName.name("TestName");

    Name name;

    @Before
    public void setup() {
        super.setup();
        name = testName;
    }

    @Override
    protected NameServiceImpl createTargetService() {
        return new NameServiceImpl(receiverCtrl);
    }

    @Test
    public void registerName() {
        targetService.receive(createStandardEnvelope(SimpleMessage.simple(NameServiceProtocol.MessageName.REGISTER_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name))
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, sender))));

        assertEquals(sender, targetService.addressByName.get(testName));
    }

    @Test
    public void lookupAddressByName() {
        targetService.addressByName.put(testName, sender);

        targetService.receive(createStandardEnvelope(SimpleMessage.simple(NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name))));
    }

    @Test
    public void unregisterName() {
        targetService.addressByName.put(testName, sender);

        targetService.receive(createStandardEnvelope(SimpleMessage.simple(NameServiceProtocol.MessageName.UNREGISTER_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name))));

        assertNull(targetService.addressByName.get(testName));
    }
}

