package org.objectagon.core.object.relationclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassProtocolImpl extends AbstractProtocol<RelationClassProtocol.Send, RelationClassProtocol.Reply> implements RelationClassProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(RELATION_CLASS_PROTOCOL, new SingletonFactory<>(RelationClassProtocolImpl::new));
    }

    public RelationClassProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected Protocol.ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(RELATION_CLASS_PROTOCOL, serverId);
    }

    @Override public RelationClassProtocol.Send createSend(Protocol.CreateSendParam createSend) {return new RelationClassProtocolSend(createSend);}
    @Override public RelationClassProtocol.Reply createReply(Protocol.CreateReplyParam createReply) {return new AbstractProtocolReply(createReply) {};}

    private class RelationClassProtocolSend extends AbstractProtocolSend implements RelationClassProtocol.Send {
        public RelationClassProtocolSend(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task createRelation(Instance.InstanceIdentity instanceIdentity) {
            return task(MessageName.CREATE_RELATION, send -> send.send(MessageName.CREATE_RELATION, MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentity)));
        }
    }
}
