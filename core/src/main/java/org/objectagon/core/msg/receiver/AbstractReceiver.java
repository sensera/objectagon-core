package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2016-01-25.
 */
public abstract class AbstractReceiver<A extends Address> implements Receiver<A> {

    private A address;
    private ReceiverCtrl receiverCtrl;

    @Override public A getAddress() {return address;}
    public Receiver.ReceiverCtrl getReceiverCtrl() {return receiverCtrl;}


    protected AbstractReceiver(ReceiverCtrl receiverCtrl) {
        //System.out.println("AbstractReceiver.AbstractReceiver "+getClass().getName()+" created!");
        this.receiverCtrl = receiverCtrl;
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<A> initializer) {
        address = createAddress(serverId, timestamp, id, initializer);
    }

    protected abstract A createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<A> initializer);

}
