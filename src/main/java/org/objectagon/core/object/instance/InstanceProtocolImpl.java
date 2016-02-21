package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.object.FieldAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceProtocol;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileTextValue.text;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceProtocolImpl extends AbstractProtocol<InstanceProtocol.Send, InstanceProtocol.Reply> implements InstanceProtocol{

    public static void registerAtServer(Server server) {
        server.registerFactory(INSTANCE_PROTOCOL, InstanceProtocolImpl::new);
    }


    public InstanceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }


    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(INSTANCE_PROTOCOL, serverId);
    }

    @Override
    public InstanceProtocol.Send createSend(CreateSendParam createSend) {
        return new InstanceProtocolSend(createSend);
    }

    @Override
    public InstanceProtocol.Reply createReply(CreateReplyParam createReply) {
        return new InstanceProtocolReply(createReply);
    }

    class InstanceProtocolSend extends AbstractProtocolSend implements InstanceProtocol.Send {

        public InstanceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        public void getValue(FieldAddress fieldAddress) {
            send(InstanceProtocol.MessageName.GET_VALUE, address(InstanceProtocol.FieldName.FIELD, fieldAddress));
        }

        public void setValue(FieldAddress fieldAddress, String value) {
            send(InstanceProtocol.MessageName.SET_VALUE, address(InstanceProtocol.FieldName.FIELD, fieldAddress), text(InstanceProtocol.FieldName.VALUE, value));
        }

    }

    class InstanceProtocolReply extends AbstractProtocolReply implements InstanceProtocol.Reply{

        public InstanceProtocolReply(CreateReplyParam replyParam) {
            super(replyParam);
        }
    }
}
