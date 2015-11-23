package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverCtrlImpl<R extends Receiver<A>, A extends Address> implements BasicReceiverCtrl<R, A> {

    private Transporter transporter;
    private Server.RegisterReceiver registerReceiver;
    private Server.ServerId serverId;
    private Receiver.CtrlId ctrlId;

    private long addressIdCounter = 0;

    public Server.ServerId getServerId() {return serverId;}
    public Receiver.CtrlId getCtrlId() {return ctrlId;}

    public BasicReceiverCtrlImpl(Transporter transporter, Server.RegisterReceiver registerReceiver, Server.ServerId serverId, Receiver.CtrlId ctrlId) {
        this.transporter = transporter;
        this.registerReceiver = registerReceiver;
        this.serverId = serverId;
        this.ctrlId = ctrlId;
    }

    public A createNewAddress(R receiver) {
        long addressId;
        synchronized (this) {
            addressId = addressIdCounter++;
        }
        A address = internalCreateNewAddress(getServerId(), ctrlId, addressId);
        registerReceiver.registerReceiver(address, receiver);
        return address;
    }

    abstract protected A internalCreateNewAddress(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId);

    public void transport(Envelope envelope) {
        transporter.transport(envelope);
    }
}
