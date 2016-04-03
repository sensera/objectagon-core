package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.RelationProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationProtocolImpl extends AbstractProtocol<RelationProtocol.Send, Protocol.Reply> implements RelationProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(RELATION_PROTOCOL, new SingletonFactory<>(RelationProtocolImpl::new));
    }

    public RelationProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, RELATION_PROTOCOL);
        createSend = RelationProtocolSend::new;
    }

    private class RelationProtocolSend extends AbstractProtocolSend implements RelationProtocol.Send {
        public RelationProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task getRelatedInstance() {
            return task(MessageName.GET_RELATED_INSTANCE, send -> send.send(MessageName.GET_RELATED_INSTANCE));
        }

        @Override
        public Task setRelatedInstance(Instance.InstanceIdentity instanceIdentity) {
            return task(MessageName.SET_RELATED_INSTANCE, send -> send.send(MessageName.SET_RELATED_INSTANCE, MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentity)));
        }
    }

}
