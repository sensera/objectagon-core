package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class StandardReceiverCtrlImpl<P extends Receiver.CreateNewAddressParams> implements StandardReceiverCtrl<P> {

    private long addressCounter = 0;
    private final Server.Ctrl server;

    protected Server.Ctrl getServer() {return server;}

    public StandardReceiverCtrlImpl(Server.Ctrl server) {
        this.server = server;
    }

    public Server.ServerId getServerId() { return server.getServerId(); }


    @Override
    public <U extends Address> U createNewAddress(Receiver<U> receiver, P params) {
        Long addressId;
        synchronized (this) { addressId = addressCounter++; }
        U address = internalCreateAddress(addressId, params);
        server.registerReceiver(address, receiver);
        return address;
    }

    protected <U extends Address> U internalCreateAddress(Long addressId, P params) {
        return (U) StandardAddress.standard(getServerId(), getCtrlId(), addressId);
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
