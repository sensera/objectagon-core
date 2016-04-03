package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Receiver;
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
    public void configure(Configurations... configurations) {
        address = createAddress(configurations);
    }

    protected abstract A createAddress(Configurations... configurations);

}
