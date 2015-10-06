package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.BasicAddress;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicReceiverCtrl implements Receiver.ReceiverCtrl<BasicAddress> {

    long serverId;
    long addressIdCounter = 0;

    public BasicReceiverCtrl(long serverId) {
        this.serverId = serverId;
    }

    public synchronized BasicAddress createNewAddress(Receiver receiver) {
        return new BasicAddress(serverId, addressIdCounter++);
    }

    public void send(Address target, Message message) {

    }
}
