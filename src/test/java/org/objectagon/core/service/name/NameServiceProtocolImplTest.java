package org.objectagon.core.service.name;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.service.AbstractProtocolTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2015-11-16.
 */

public class NameServiceProtocolImplTest extends AbstractProtocolTest {

    NameServiceProtocolImpl protocol;
    NameServiceProtocol.Send send;
    Name name;
    Address address;
    Address senderAddress;

    @Before
    public void setup() {
        super.setup();
        name = mock(Name.class);
        address = mock(Address.class);
        senderAddress = mock(Address.class);
        protocol = new NameServiceProtocolImpl(receiverCtrl);
        protocol.initialize(mock(Server.ServerId.class), 100, 0, null);
        Protocol.CreateSendParam createSendParam = mock(Protocol.CreateSendParam.class);

        when(createSendParam.getComposer()).thenReturn(composer);
        when(composer.getSenderAddress()).thenReturn(senderAddress);
        when(composer.alternateReceiver(any(Receiver.class))).thenReturn(composer);

        send = protocol.createSend(createSendParam);
    }

    @Test
    public void registerName()  {
        startTaskAndVerifySentEvelope(
                send.registerName(address, name),
                message -> {
                    assertEquals(message.getName(), NameServiceProtocol.MessageName.REGISTER_NAME);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), address);
                    assertEquals(message.getValue(StandardField.NAME).asName(), name);
                });
    }

    @Test
    public void unregisterName()  {
        startTaskAndVerifySentEvelope(
                send.unregisterName(name),
                message -> {
                    assertEquals(message.getName(), NameServiceProtocol.MessageName.UNREGISTER_NAME);
                    assertEquals(message.getValue(StandardField.NAME).asName(), name);
                });
    }

    @Test
    public void lookupName() {
        startTaskAndVerifySentEvelope(
                send.lookupAddressByName(name),
                (message) -> {
                    assertEquals(message.getName(), NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME);
                    assertEquals(message.getValue(StandardField.NAME).asName(), name);
                });
    }



}