package org.objectagon.core.object.method;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodProtocolImpl extends AbstractProtocol<MethodProtocol.Send, Protocol.Reply> implements MethodProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(METHOD_PROTOCOL, new SingletonFactory<>(MethodProtocolImpl::new));
    }

    public MethodProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, METHOD_PROTOCOL);
        createSend = MethodProtocolSend::new;
    }

    private class MethodProtocolSend extends AbstractProtocolSend implements MethodProtocol.Send {
        public MethodProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task getCode() {
            return task(MessageName.GET_CODE, send -> send.send(MessageName.GET_CODE));
        }

        @Override
        public Task setCode(String code) {
            return task(MessageName.SET_CODE, send -> send.send(MessageName.SET_CODE, MessageValue.text(Method.CODE, code)));
        }

        @Override
        public Task invoke(MessageValue... params) {
            return task(MessageName.INVOKE, send -> send.send(MessageName.INVOKE, MessageValue.values(Method.CODE, params)));
        }
    }

}
