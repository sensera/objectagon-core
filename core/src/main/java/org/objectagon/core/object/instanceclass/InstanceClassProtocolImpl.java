package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.*;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassProtocolImpl extends AbstractProtocol<InstanceClassProtocol.Send, Protocol.Reply> implements InstanceClassProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(INSTANCE_CLASS_PROTOCOL, new SingletonFactory<>(InstanceClassProtocolImpl::new));
    }

    public InstanceClassProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, INSTANCE_CLASS_PROTOCOL);
        createSend = InstanceClassProtocolSend::new;
    }

    private class InstanceClassProtocolSend extends AbstractProtocolSend implements InstanceClassProtocol.Send {
        public InstanceClassProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task addField() {
            return task(MessageName.ADD_FIELD, send -> send.send(MessageName.ADD_FIELD));
        }

        @Override
        public Task addRelation(RelationClass.RelationType type, InstanceClass.InstanceClassIdentity relatedClass) {
            return task(MessageName.ADD_RELATION, send -> send.send(
                    MessageName.ADD_RELATION,
                    text(RelationClass.RELATION_TYPE, type.name()),
                    address(RelationClass.RELATION_CLASS_IDENTITY, relatedClass)));
        }

        @Override
        public Task createInstance() {
            return task(MessageName.CREATE_INSTANCE, send -> send.send(MessageName.CREATE_INSTANCE));
        }

        @Override
        public Task getName() {
            return task(MessageName.GET_NAME, send -> send.send(MessageName.GET_NAME));
        }

        @Override
        public Task setName(InstanceClass.InstanceClassName name) {
            return task(MessageName.SET_NAME, send -> send.send(MessageName.SET_NAME, name(InstanceClass.INSTANCE_CLASS_NAME, name)));
        }
    }
}
