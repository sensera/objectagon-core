package org.objectagon.core.service.name;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.ReactorImpl;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.task.Task;

import static org.mockito.Mockito.*;

/**
 * Created by christian on 2015-11-16.
 */

public class NameServiceProtocolImplTest extends AbstractProtocolTest {

    Server.ServerId serverId;
    Server server;
    Server.Ctrl serverCtrl;
    StandardReceiverCtrl ctrl;
    NameServiceImpl nameService;
    StandardAddress nameServiceAddress;
    Receiver receiver;
    Composer composer;
    Protocol.SessionOwner sessionOwner;
    Transporter transportToService = envelope -> nameService.receive(envelope);
    StandardProtocol.StandardSession standardProtocolSession;
    NameServiceProtocolImpl protocol;
    NameServiceProtocol.Session session;

    Address address = mock(Address.class);
    Name name = mock(Name.class);

    @Before
    public void setup() {
        serverId = LocalServerId.local("TestServer");
        nameServiceAddress = StandardAddress.standard(serverId, NameServiceImpl.NAME_SERVICE_CTRL_NAME, 1);
        server = mock(Server.class);
        serverCtrl = mock(Server.Ctrl.class);
        ctrl = mock(StandardReceiverCtrl.class);
        receiver = mock(Receiver.class);
        sessionOwner = mock(Protocol.SessionOwner.class);
        standardProtocolSession = mock(StandardProtocol.StandardSession.class);

        composer = StandardComposer.create(receiver, address);

        when(ctrl.getReactor()).thenReturn(new ReactorImpl());
        when(ctrl.createSession(eq(StandardProtocol.STANDARD_PROTOCOL), any(Composer.class))).thenReturn(standardProtocolSession);
        when(sessionOwner.getComposer()).thenReturn(composer);
        when(sessionOwner.getTransporter()).thenReturn(transportToService);

        nameService = new NameServiceImpl(ctrl);

        protocol = new NameServiceProtocolImpl(serverId);
        session = protocol.createSession(sessionOwner);
    }

    @Test
    public void registerName() {
        session.registerName(address, name);

        verify(standardProtocolSession, atLeastOnce()).replyOk();
    }

    @Test
    public void lookupName() throws FailedException {
        registerName();

        session.lookupAddressByName(name).start();

        verify(standardProtocolSession, atLeastOnce()).replyWithParam(any(Message.Value.class));
    }

}