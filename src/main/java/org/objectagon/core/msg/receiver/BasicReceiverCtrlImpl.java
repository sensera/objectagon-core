package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverCtrlImpl<P extends Receiver.CreateNewAddressParams> implements BasicReceiverCtrl<P> {

    private Transporter transporter;
    private final Protocol.SessionFactory sessionFactory;
    private Server.RegisterReceiver registerReceiver;
    private Server.ServerId serverId;
    private Receiver.CtrlId ctrlId;

    private long addressIdCounter = 0;

    public Server.ServerId getServerId() {return serverId;}
    public Receiver.CtrlId getCtrlId() {return ctrlId;}

    public BasicReceiverCtrlImpl(Transporter transporter, Protocol.SessionFactory sessionFactory, Server.RegisterReceiver registerReceiver, Server.ServerId serverId, Receiver.CtrlId ctrlId) {
        this.transporter = transporter;
        this.sessionFactory = sessionFactory;
        this.registerReceiver = registerReceiver;
        this.serverId = serverId;
        this.ctrlId = ctrlId;
    }

    public <A extends Address> A createNewAddress(Receiver<A> receiver, P params) {
        long addressId;
        synchronized (this) {
            addressId = addressIdCounter++;
        }
        A address = internalCreateNewAddress(getServerId(), ctrlId, addressId, params);
        registerReceiver.registerReceiver(address, receiver);
        return address;
    }

    abstract protected <A extends Address> A internalCreateNewAddress(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId, P param);

    @Override
    public <U extends Protocol.Session> U createSession(Protocol.ProtocolName protocolName, Composer composer) {
        return sessionFactory.createSession(protocolName, composer);
    }

    public void transport(Envelope envelope) {
        transporter.transport(envelope);
    }
}
