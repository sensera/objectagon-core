package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
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
    }
}
