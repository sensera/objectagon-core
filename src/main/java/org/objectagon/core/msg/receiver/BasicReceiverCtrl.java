package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicReceiverCtrl implements Receiver.ReceiverCtrl<StandardAddress> {

    private long serverId;
    private long ctrlId;
    private long addressIdCounter = 0;
    private Transporter transporter;

    public BasicReceiverCtrl(Transporter transporter, long ctrlId, long serverId) {
        this.transporter = transporter;
        this.ctrlId = ctrlId;
        this.serverId = serverId;
    }

    public synchronized StandardAddress createNewAddress(Receiver receiver) {
        return new StandardAddress(serverId, ctrlId, addressIdCounter++);
    }

    public void transport(Envelope envelope) {
        transporter.transport(envelope);
    }
}
