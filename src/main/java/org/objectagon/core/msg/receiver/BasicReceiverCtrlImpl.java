package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverCtrlImpl<U extends StandardAddress> implements BasicReceiverCtrl<U> {

    private long serverId;
    private long ctrlId;
    private long addressIdCounter = 0;
    private Transporter transporter;

    public BasicReceiverCtrlImpl(Transporter transporter, long ctrlId, long serverId) {
        this.transporter = transporter;
        this.ctrlId = ctrlId;
        this.serverId = serverId;
    }

    public U createNewAddress(Receiver receiver) {
        return internalCreateNewAddress(serverId, ctrlId, addressIdCounter++);
    }

    abstract protected U internalCreateNewAddress(long ctrlId, long serverId, long addressId);

    public void transport(Envelope envelope) {
        transporter.transport(envelope);
    }
}
