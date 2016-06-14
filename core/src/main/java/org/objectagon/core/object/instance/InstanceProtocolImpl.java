package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instanceclass.MethodMessageValueTransform;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.Arrays;
import java.util.List;

import static org.objectagon.core.msg.message.MessageValue.address;


/**
 * Created by christian on 2015-10-20.
 */
public class InstanceProtocolImpl extends AbstractProtocol<InstanceProtocol.Send, Protocol.Reply> implements InstanceProtocol {

    static final MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

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

    @Override
    public Internal createInternal(CreateSendParam createSendParam) {
        return new InstanceProtocolInternal(createSendParam);
    }

    class InstanceProtocolSend extends AbstractProtocolSend implements InstanceProtocol.Send {

        public InstanceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task getValue(Field.FieldIdentity fieldIdentity) {
            return task(MessageName.GET_VALUE, send -> send.send(MessageName.GET_VALUE, address(Field.FIELD_IDENTITY, fieldIdentity)));
        }

        @Override
        public Task setValue(Field.FieldIdentity fieldIdentity, Message.Value value) {
            return task(MessageName.SET_VALUE, send -> send.send(MessageName.SET_VALUE, address(Field.FIELD_IDENTITY, fieldIdentity), MessageValue.values(FieldValue.VALUE, value)));
        }

        @Override
        public Task getRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
            return task(MessageName.GET_RELATION, send -> send.send(MessageName.GET_RELATION, address(RelationClass.RELATION_CLASS_IDENTITY, relationClassIdentity)));
        }

        @Override
        public Task addRelation(RelationClass.RelationClassIdentity relationClassIdentity, Instance.InstanceIdentity instance) {
            return task(MessageName.ADD_RELATION, send -> send.send(MessageName.ADD_RELATION, address(RelationClass.RELATION_CLASS_IDENTITY, relationClassIdentity), address(instance)));
        }

        @Override
        public Task removeRelation(RelationClass.RelationClassIdentity relationClassIdentity, Instance.InstanceIdentity instance) {
            return task(MessageName.REMOVE_RELATION, send -> send.send(MessageName.REMOVE_RELATION, address(RelationClass.RELATION_CLASS_IDENTITY, relationClassIdentity), address(instance)));
        }

        @Override
        public Task invokeMethod(Method.MethodIdentity methodIdentity, KeyValue<Method.ParamName, Message.Value>... paramValues) {
            return task(MessageName.INVOKE_METHOD, send -> send.send(
                    MessageName.INVOKE_METHOD,
                    address(Method.METHOD_IDENTITY, methodIdentity),
                    methodMessageValueTransform.createValuesTransformer().transform(Arrays.asList(paramValues))
            ));
        }

        @Override
        public Task invokeMethod(Method.MethodIdentity methodIdentity, List<KeyValue<Method.ParamName, Message.Value>> paramValues) {
            return task(MessageName.INVOKE_METHOD, send -> send.send(
                    MessageName.INVOKE_METHOD,
                    address(Method.METHOD_IDENTITY, methodIdentity),
                    methodMessageValueTransform.createValuesTransformer().transform(paramValues)
            ));
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

    class InstanceProtocolInternal extends AbstractProtocolSend implements InstanceProtocol.Internal {
        public InstanceProtocolInternal(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task setRelation(Relation.RelationIdentity relationIdentity) {
            return task(MessageName.SET_RELATION, send -> send.send(MessageName.SET_RELATION, MessageValue.address(StandardField.ADDRESS, relationIdentity)));
        }

        @Override
        public Task dropRelation(Relation.RelationIdentity relationIdentity) {
            return task(MessageName.DROP_RELATION, send -> send.send(MessageName.DROP_RELATION, MessageValue.address(StandardField.ADDRESS, relationIdentity)));
        }
    }
}
