package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-14.
 */
public class FieldProtocolImpl extends AbstractProtocol<FieldProtocol.Send, Protocol.Reply> implements FieldProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(FIELD_PROTOCOL, new SingletonFactory<>(FieldProtocolImpl::new));
    }

    public FieldProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, FIELD_PROTOCOL);
        createSend = FieldProtocolSend::new;
    }

    @Override
    public Forward createForward(CreateSendParam createSend) {
        return new FieldProtocolForward(createSend);
    }

    private class FieldProtocolSend extends AbstractProtocolSend implements FieldProtocol.Send {
        public FieldProtocolSend(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task getName() {
            return task(MessageName.GET_NAME, send -> send.send(MessageName.GET_NAME) );
        }

        @Override
        public Task setName(Field.FieldName name) {
            return task(MessageName.SET_NAME, send -> send.send(MessageName.SET_NAME, MessageValue.name(Field.FIELD_NAME, name)) );
        }

        @Override
        public Task getType() {
            return task(MessageName.GET_TYPE, send -> send.send(MessageName.GET_TYPE) );
        }

        @Override
        public Task setType(Field.FieldType type) {
            return task(MessageName.SET_TYPE, send -> send.send(MessageName.SET_TYPE, MessageValue.name(Field.FIELD_TYPE, type)) );
        }

        @Override
        public Task getDefaultValue() {
            return task(MessageName.GET_DEFAULT_VALUE, send -> send.send(MessageName.GET_DEFAULT_VALUE) );
        }

        @Override
        public Task setDefaultValue(Message.Value value) {
            return task(MessageName.SET_DEFAULT_VALUE, send -> send.send(MessageName.SET_DEFAULT_VALUE, MessageValue.values(FieldValue.VALUE, value)));
        }

        @Override
        public Task createFieldValue(Instance.InstanceIdentity instanceIdentity, Message.Value value) {
            return task(MessageName.CREATE_FIELD_VALUE, send -> send.send(
                    MessageName.CREATE_FIELD_VALUE,
                    MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentity),
                    MessageValue.values(FieldValue.VALUE, value)));
        }

    }

    private class FieldProtocolForward extends AbstractProtocolForward implements FieldProtocol.Forward {
        public FieldProtocolForward(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public void getDefaultValue() {
            send(MessageName.GET_DEFAULT_VALUE);
        }
    }
}
