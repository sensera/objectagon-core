package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.address;
import static org.objectagon.core.msg.message.MessageValue.name;


/**
 * Created by christian on 2015-10-20.
 */
public class InstanceProtocolImpl extends AbstractProtocol<InstanceProtocol.Send, Protocol.Reply> implements InstanceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(INSTANCE_PROTOCOL, new SingletonFactory<>(InstanceProtocolImpl::new));
    }


    public InstanceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, INSTANCE_PROTOCOL);
        createSend = InstanceProtocolSend::new;
    }

    @Override
    public Alter createAlter(CreateSendParam createSend) {
        return new InstanceProtocolAlter(createSend);
    }

    class InstanceProtocolSend extends AbstractProtocolSend implements InstanceProtocol.Send {

        public InstanceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task getValue(Field.FieldName name) {
            return task(MessageName.GET_VALUE, send -> send.send(MessageName.GET_VALUE, name(Field.FIELD_NAME, name)));
        }

        @Override
        public Task setValue(Field.FieldName name, Message.Value value) {
            return task(MessageName.SET_VALUE, send -> send.send(MessageName.SET_VALUE, name(Field.FIELD_NAME, name), value));
        }

        @Override
        public Task getRelation(RelationClass.RelationName name) {
            return task(MessageName.GET_RELATION, send -> send.send(MessageName.GET_RELATION, name(RelationClass.RELATION_NAME, name)));
        }

        @Override
        public Task addRelation(RelationClass.RelationName name, Instance.InstanceIdentity instance) {
            return task(MessageName.ADD_RELATION, send -> send.send(MessageName.ADD_RELATION, name(RelationClass.RELATION_NAME, name), address(instance)));
        }

        @Override
        public Task removeRelation(RelationClass.RelationName name, Instance.InstanceIdentity instance) {
            return task(MessageName.REMOVE_RELATION, send -> send.send(MessageName.REMOVE_RELATION, name(RelationClass.RELATION_NAME, name), address(instance)));
        }
    }

    class InstanceProtocolAlter extends AbstractProtocolSend implements InstanceProtocol.Alter {
        public InstanceProtocolAlter(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task addField(Field.FieldIdentity fieldIdentity) {
            return task(MessageName.ADD_FIELD, send -> send.send(MessageName.ADD_FIELD, address(fieldIdentity)));
        }

        @Override
        public Task removeField(Field.FieldIdentity fieldIdentity) {
            return task(MessageName.REMOVE_FIELD, send -> send.send(MessageName.REMOVE_FIELD, address(fieldIdentity)));
        }

        @Override
        public Task addRelationClass(RelationClass.RelationClassIdentity relationClassIdentity) {
            return task(MessageName.ADD_RELATION_CLASS, send -> send.send(MessageName.ADD_FIELD, address(relationClassIdentity)));
        }

        @Override
        public Task removeRelationClass(RelationClass.RelationClassIdentity relationClassIdentity) {
            return task(MessageName.REMOVE_RELATION_CLASS, send -> send.send(MessageName.REMOVE_RELATION_CLASS, address(relationClassIdentity)));
        }
    }
}
