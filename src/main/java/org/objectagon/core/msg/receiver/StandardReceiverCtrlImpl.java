package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class StandardReceiverCtrlImpl<R extends Receiver<A>, A extends Address> implements StandardReceiverCtrl<R, A> {

    private final Server.Ctrl server;

    public StandardReceiverCtrlImpl(Server.Ctrl server) {
        this.server = server;
    }

    public Server.ServerId getServerId() { return server.getServerId(); }

    protected void registerReceiver(A address, R receiver) {
        server.registerReceiver(address, receiver);
    }

    @Override
    public <R extends Reactor.Trigger> Reactor<R>  getReactor() {
        return new ReactorImpl<>();
    }

    @Override
    public void transport(Envelope envelope) {
        server.transport(envelope);
    }


    @Override
    public <U extends Protocol.Session> U createSession(Protocol.ProtocolName protocolName, Composer composer) {
        return server.createSession(protocolName, composer);
    }
}
