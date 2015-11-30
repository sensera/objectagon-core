package org.objectagon.core.service.name;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.msg.receiver.StandardReceiverCtrlImpl;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.server.ServerImpl;
import sun.net.spi.nameservice.NameService;

import static org.mockito.Mockito.*;
import static org.objectagon.core.server.LocalServerId.local;

/**
 * Created by christian on 2015-11-16.
 */

public class NameServiceProtocolImplTest extends AbstractProtocolTest {

    Server.ServerId serverId;
    Server server;
    Server.Ctrl serverCtrl;
    StandardReceiverCtrl<NameServiceImpl,StandardAddress> ctrl;
    NameServiceImpl nameService;
    StandardAddress nameServiceAddress;
    Composer composer;
    Protocol.SessionOwner sessionOwner;
    Transporter transportToService = envelope -> nameService.receive(envelope);

    @Before
    public void setup() {
        serverId = LocalServerId.local("TestServer");
        nameServiceAddress = StandardAddress.standard(serverId, NameServiceImpl.NAME_SERVICE_CTRL_NAME, 1);
        server = mock(Server.class);
        serverCtrl = mock(Server.Ctrl.class);
        ctrl = mock(StandardReceiverCtrl.class);
        composer = mock(Composer.class);
        sessionOwner = mock(Protocol.SessionOwner.class);

        nameService = new NameServiceImpl(ctrl);
    }

    @Test
    public void registerName() {
        NameServiceProtocolImpl protocol = new NameServiceProtocolImpl();
        NameServiceProtocol.Session session = protocol.createSession(transportToService, composer, sessionOwner);

        Address address = mock(Address.class);
        Name name = mock(Name.class);
        session.registerName(address, name);
    }
}