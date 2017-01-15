package core.service.name;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.service.AbstractProtocolTest;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.service.name.NameServiceProtocolImpl;

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
        protocol.configure();
        Protocol.CreateSendParam createSendParam = mock(Protocol.CreateSendParam.class);

        when(createSendParam.getComposer()).thenReturn(composer);
        when(composer.getSenderAddress()).thenReturn(senderAddress);
        when(composer.alternateReceiver(any(Receiver.class))).thenReturn(composer);

        send = protocol.createSend(createSendParam);
    }

    @Test
    @Ignore
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
    @Ignore
    public void unregisterName()  {
        startTaskAndVerifySentEvelope(
                send.unregisterName(name),
                message -> {
                    assertEquals(message.getName(), NameServiceProtocol.MessageName.UNREGISTER_NAME);
                    assertEquals(message.getValue(StandardField.NAME).asName(), name);
                });
    }

    @Test
    @Ignore
    public void lookupName() {
        startTaskAndVerifySentEvelope(
                send.lookupAddressByName(name),
                (message) -> {
                    assertEquals(message.getName(), NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME);
                    assertEquals(message.getValue(StandardField.NAME).asName(), name);
                });
    }



}