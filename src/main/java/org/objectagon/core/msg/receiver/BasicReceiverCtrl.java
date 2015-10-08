package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.BasicAddress;
import org.objectagon.core.msg.envelope.StandardEnvelope;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicReceiverCtrl implements Receiver.ReceiverCtrl<BasicAddress> {

    private long serverId;
    private long addressIdCounter = 0;
    private Transporter transporter;

    public BasicReceiverCtrl(Transporter transporter, long serverId) {
        this.transporter = transporter;
        this.serverId = serverId;
    }

    public synchronized BasicAddress createNewAddress(Receiver receiver) {
        return new BasicAddress(serverId, addressIdCounter++);
    }

    public void transport(Envelope envelope) {
        transporter.transport(envelope);
    }
}
