package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class StandardReceiverCtrlImpl<R extends Receiver<A>, A extends Address> implements StandardReceiverCtrl<R, A> {

    private final Server server;

    public StandardReceiverCtrlImpl(Server server) {
        this.server = server;
    }

    protected Server.ServerId getServerId() { return server.getServerId(); }

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

}
