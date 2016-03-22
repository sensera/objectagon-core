package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.FieldValueProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-04.
 */
public class FieldValueProtocolImpl extends AbstractProtocol<FieldValueProtocol.Send, FieldValueProtocol.Reply> implements FieldValueProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(FIELD_VALUE_PROTOCOL, new SingletonFactory<>(FieldValueProtocolImpl::new));
    }

    public FieldValueProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected Protocol.ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(FIELD_VALUE_PROTOCOL, serverId);
    }

    @Override public FieldValueProtocol.Send createSend(Protocol.CreateSendParam createSend) {
        return new FieldValueProtocolSend(createSend);
    }
    @Override public Protocol.Reply createReply(Protocol.CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }
    @Override public Forward createForward(Protocol.CreateSendParam createSend) {
        return new FieldValueProtocolForward(createSend);
    }

    private class FieldValueProtocolSend extends AbstractProtocolSend implements FieldValueProtocol.Send {
        public FieldValueProtocolSend(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override public Task getValue() {
            return task(MessageName.GET_VALUE, send -> send.send(MessageName.GET_VALUE));
        }

        @Override public Task setValue(Message.Value value) {
            return task(MessageName.SET_VALUE, send -> send.send(MessageName.SET_VALUE, value));
        }
    }

    private class FieldValueProtocolForward extends AbstractProtocolForward implements FieldValueProtocol.Forward {
        public FieldValueProtocolForward(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override public void getValue() {
            transport(composer.create(MessageName.GET_VALUE));
        }

        @Override public void setValue(Message.Value value) {
            transport(composer.create(MessageName.SET_VALUE, value));
        }
    }
}
