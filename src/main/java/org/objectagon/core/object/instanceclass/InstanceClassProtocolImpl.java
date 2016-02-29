package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.object.InstanceClassProtocol;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassProtocolImpl extends AbstractProtocol<InstanceClassProtocol.Send, InstanceClassProtocol.Reply> implements InstanceClassProtocol {

    public InstanceClassProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(INSTANCE_CLASS_PROTOCOL, serverId);
    }

    @Override
    public InstanceClassProtocol.Send createSend(CreateSendParam createSend) {
        return null;
    }

    @Override
    public InstanceClassProtocol.Reply createReply(CreateReplyParam createReply) {
        return null;
    }
}
