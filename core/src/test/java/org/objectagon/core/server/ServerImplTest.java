package org.objectagon.core.server;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.envelope.StandardEnvelope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2016-01-02.
 */
public class ServerImplTest {

    private ServerImpl server;
    private LocalServerId serverId;

    @Before
    public void setUp() throws Exception {
        serverId = LocalServerId.local("name");
        server = new ServerImpl(serverId, () -> 100);
        server.envelopeProcessor = envelope -> envelope.targets(server::processEnvelopeTarget); // Bypass threaded processing with direct envelope processing
    }

    @Test
    @Ignore
    public void testRegisterReceiver() {
        Name name = mock(Name.class);
        Receiver receiver = mock(Receiver.class);
        Receiver.Configurations configurations = mock(Receiver.Configurations.class);
        when(receiver.getAddress()).thenReturn(mock(Address.class));

        server.registerFactory(name, receiverCtrl -> receiver);

        assertEquals(receiver, server.createReceiver(name, configurations));

        verify(receiver, times(1)).configure();
    }

    @Test
    @Ignore
    public void testCreateReceiverFail() {
        Receiver.Configurations configurations = mock(Receiver.Configurations.class);;

        Name nameNotRegisteredAtServer = mock(Name.class);
        try {
            server.createReceiver(nameNotRegisteredAtServer, configurations);
            fail("Receiver non existent!");
        } catch (SevereError e) {
            if (ErrorClass.SERVER.equals(e.getErrorClass()) &&
                ErrorKind.RECEIVER_NOT_FOUND.equals(e.getErrorKind()) &&
                e.getParams().iterator().next().asName().equals(nameNotRegisteredAtServer)
                    )
                return;

            throw e;
        }
    }

    @Test
    @Ignore
    public void testTransportEnvelope() {
        Address target = mock(Address.class);
        Receiver receiver = mock(Receiver.class);

        server.registerReceiver(target, receiver);

        server.transport(new StandardEnvelope(mock(Address.class), target, mock(Message.class), mock(Message.Values.class)));
    }

    @Test
    @Ignore
    public void testTransportEnvelopeToFactoryCreatedReceiver() {
        Name name = mock(Name.class);
        Address target = mock(Address.class);
        Receiver.Configurations configurations = mock(Receiver.Configurations.class);;

        server.registerFactory(name, receiverCtrl -> {
            Receiver receiver = mock(Receiver.class);
            when(receiver.getAddress()).thenReturn(target);
            return receiver;
        });

        server.createReceiver(name, configurations);

        server.transport(new StandardEnvelope(mock(Address.class), target, mock(Message.class), mock(Message.Values.class)));
    }

    @Test
    @Ignore
    public void testTransportEnvelopeFail() {
        Address unknownTarget = mock(Address.class);
        try {
        server.transport(new StandardEnvelope(mock(Address.class), unknownTarget, mock(Message.class), mock(Message.Values.class)));
            fail("Target non existent!");
        } catch (SevereError e) {
            if (ErrorClass.ENVELOPE.equals(e.getErrorClass()) &&
                    ErrorKind.UNKNOWN_TARGET.equals(e.getErrorKind()) &&
                    e.getParams().iterator().next().asAddress().equals(unknownTarget)
                    )
                return;

            throw e;
        }
    }

}