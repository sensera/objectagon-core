package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.address;
import static org.objectagon.core.msg.message.MessageValue.name;
import static org.objectagon.core.msg.message.MessageValue.text;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassProtocolImpl extends AbstractProtocol<InstanceClassProtocol.Send, Protocol.Reply> implements InstanceClassProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(INSTANCE_CLASS_PROTOCOL, new SingletonFactory<>(InstanceClassProtocolImpl::new));
    }

    public InstanceClassProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(INSTANCE_CLASS_PROTOCOL, serverId);
    }

    @Override
    public InstanceClassProtocol.Send createSend(CreateSendParam createSend) {
        return new InstanceClassProtocolSend(createSend);
    }

    @Override
    public InstanceClassProtocol.Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }

    private class InstanceClassProtocolSend extends AbstractProtocolSend implements InstanceClassProtocol.Send {
        public InstanceClassProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task addField(Field.FieldName name) {
            return task(MessageName.ADD_FIELD, send -> send.send(MessageName.ADD_FIELD, name(Field.FIELD_NAME, name)));
        }

        @Override
        public Task addRelation(RelationClass.RelationName name, RelationClass.RelationType type, InstanceClass.InstanceClassIdentity relatedClass) {
            return task(MessageName.ADD_RELATION, send -> send.send(
                    MessageName.ADD_RELATION,
                    name(RelationClass.RELATION_NAME, name),
                    text(RelationClass.RELATION_TYPE, type.name()),
                    address(RelationClass.RELATION_CLASS_IDENTITY, relatedClass)));
        }

        @Override
        public Task createInstance() {
            return task(MessageName.CREATE_INSTANCE, send -> send(MessageName.CREATE_INSTANCE));
        }
    }
}
