package org.objectagon.core.service.name;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNameValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.AbstractProtocolTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2016-02-16.
 */
public class NameServiceTest extends AbstractProtocolTest {

    private final Name testName = StandardName.name("TestName");

    NameServiceImpl nameService;
    Address sender;
    Address target;
    Name name;
    NameServiceProtocol nameServiceProtocol;
    StandardProtocol standardProtocol;
    StandardProtocol.StandardReply standardReply;

    @Before
    public void setup() {
        super.setup();
        sender = mock(Address.class);
        target = mock(Address.class);
        name = testName;
        nameServiceProtocol = mock(NameServiceProtocol.class);
        nameService = new NameServiceImpl(receiverCtrl);
        nameService.initialize(mock(Server.ServerId.class), 100, 0, null);
        standardProtocol = mock(StandardProtocol.class);
        standardReply = mock(StandardProtocol.StandardReply.class);

        when(receiverCtrl.createReceiver(eq(NameServiceProtocol.NAME_SERVICE_PROTOCOL), isNull(Receiver.Initializer.class))).thenReturn(nameServiceProtocol);
        when(receiverCtrl.createReceiver(eq(StandardProtocol.STANDARD_PROTOCOL), isNull(Receiver.Initializer.class))).thenReturn(standardProtocol);
        when(standardProtocol.createReply(any(Protocol.CreateReplyParam.class))).thenReturn(standardReply);
    }

    @Test
    public void registerName() {
        SimpleMessage message = SimpleMessage.simple(NameServiceProtocol.MessageName.REGISTER_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name))
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, sender));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message, mock(Message.Values.class));
        nameService.receive(envelope);

        assertEquals(sender, nameService.addressByName.get(testName));
    }

    @Test
    public void lookupAddressByName() {
        nameService.addressByName.put(testName, sender);

        SimpleMessage message = SimpleMessage.simple(NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message, mock(Message.Values.class));
        nameService.receive(envelope);
    }

    @Test
    public void unregisterName() {
        nameService.addressByName.put(testName, sender);

        SimpleMessage message = SimpleMessage.simple(NameServiceProtocol.MessageName.UNREGISTER_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message, mock(Message.Values.class));
        nameService.receive(envelope);

        assertNull(nameService.addressByName.get(testName));
    }
}

