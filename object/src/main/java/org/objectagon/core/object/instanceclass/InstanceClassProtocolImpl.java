package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

import static org.objectagon.core.msg.message.MessageValue.*;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassProtocolImpl extends AbstractProtocol<InstanceClassProtocol.Send, Protocol.Reply> implements InstanceClassProtocol {

    private static MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

    public static void registerAt(Server server) {
        server.registerFactory(INSTANCE_CLASS_PROTOCOL, new SingletonFactory<>(InstanceClassProtocolImpl::new));
    }

    public InstanceClassProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, INSTANCE_CLASS_PROTOCOL);
        createSend = InstanceClassProtocolSend::new;
    }

    @Override
    public Internal createInternal(CreateSendParam createSendParam) {
        return new InstanceClassProtocolInternal(createSendParam);
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
            if (relatedClass == null)
                throw new NullPointerException("relatedClass is null!");
            return task(MessageName.ADD_RELATION, send -> send.send(
                    MessageName.ADD_RELATION,
                    text(RelationClass.RELATION_TYPE, type.name()),
                    address(RelationClass.INSTANCE_CLASS_TO, relatedClass)));
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

        @Override
        public Task addInstanceAlias(Instance.InstanceIdentity instanceIdentity, Name aliasName) {
            if (instanceIdentity == null)
                throw new NullPointerException("instanceIdentity is null!");
            if (aliasName == null)
                throw new NullPointerException("aliasName is null!");
            return task(MessageName.ADD_INSTANCE_ALIAS, send -> send.send(MessageName.ADD_INSTANCE_ALIAS, address(Instance.INSTANCE_IDENTITY,instanceIdentity), name(ALIAS_NAME, aliasName)));
        }

        @Override
        public Task removeInstanceAlias(Name aliasName) {
            return task(MessageName.REMOVE_INSTANCE_ALIAS, send -> send.send(MessageName.REMOVE_INSTANCE_ALIAS, name(ALIAS_NAME, aliasName)));
        }

        @Override
        public Task getInstanceByAlias(Name aliasName) {
            return task(MessageName.GET_INSTANCE_BY_ALIAS, send -> send.send(MessageName.GET_INSTANCE_BY_ALIAS, name(ALIAS_NAME, aliasName)));
        }

        @Override
        public Task addMethod(Method.MethodIdentity methodIdentity,
                              List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings,
                              List<KeyValue<Method.ParamName, Message.Value>> defaultValues) {
            return task(MessageName.ADD_METHOD,
                    send -> send.send(
                            MessageName.ADD_METHOD,
                            address(Method.METHOD_IDENTITY, methodIdentity),
                            methodMessageValueTransform.createFieldMappingsTransformer().transform(fieldMappings),
                            methodMessageValueTransform.createValuesTransformer().transform(defaultValues)
                    ));
        }

        @Override
        public Task removeMethod(Method.MethodIdentity methodIdentity) {
            return task(MessageName.REMOVE_METHOD, send -> send.send(MessageName.REMOVE_METHOD, address(Method.METHOD_IDENTITY, methodIdentity)));
        }

        @Override
        public Task invokeMethod(Method.MethodIdentity methodIdentity, Instance.InstanceIdentity instanceIdentity, List<KeyValue<Method.ParamName, Message.Value>> paramValues) {
            return task(MessageName.INVOKE_METHOD, send -> send.send(
                    MessageName.INVOKE_METHOD,
                    address(Method.METHOD_IDENTITY, methodIdentity),
                    address(Instance.INSTANCE_IDENTITY, instanceIdentity),
                    methodMessageValueTransform.createValuesTransformer().transform(paramValues)
            ));
        }
    }

    private class InstanceClassProtocolInternal extends AbstractProtocolSend implements Internal {
        public InstanceClassProtocolInternal(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task setRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
            return task(MessageName.SET_RELATION, send -> send.send(MessageName.SET_RELATION, address(RelationClass.RELATION_CLASS_IDENTITY, relationClassIdentity)));
        }
    }
}
