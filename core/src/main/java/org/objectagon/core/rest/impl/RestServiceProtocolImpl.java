package org.objectagon.core.rest.impl;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.rest.RestServiceProtocol;
import org.objectagon.core.task.Task;

import java.util.List;

/**
 * Created by christian on 2016-03-16.
 */
public class RestServiceProtocolImpl extends AbstractProtocol<RestServiceProtocol.Send, Protocol.Reply> implements RestServiceProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(REST_SERVICE_PROTOCOL, new SingletonFactory<>(RestServiceProtocolImpl::new));
    }

    public RestServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, REST_SERVICE_PROTOCOL);
        createSend = RestServiceProtocolSend::new;
    }

    @Override
    public SendContent createSendContent(CreateSendParam createSend) {
        return new RestServiceProtocolSendContent(createSend);
    }

    private class RestServiceProtocolSend extends AbstractProtocolSend implements RestServiceProtocol.Send {
        public RestServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task httpSimpleCall(MethodType methodType, String url, List<Message.Value> headers) {
            return task(MessageName.HTTP_SIMPLE_CALL, send -> send.send(
                    MessageName.HTTP_SIMPLE_CALL,
                    MessageValue.name(METHOD_TYPE, methodType),
                    MessageValue.text(URL, url),
                    MessageValue.values(HEADERS, headers)));
        }
    }

    private class RestServiceProtocolSendContent extends AbstractProtocolSend implements RestServiceProtocol.SendContent {
        public RestServiceProtocolSendContent(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task httpCallStart(MethodType methodType, String url, List<Message.Value> headers, byte[] content, Long position, Long length) {
            return task(MessageName.HTTP_SIMPLE_CALL, send -> send.send(
                    MessageName.HTTP_SIMPLE_CALL,
                    MessageValue.name(METHOD_TYPE, methodType),
                    MessageValue.text(URL, url),
                    MessageValue.values(HEADERS, headers),
                    MessageValue.blob(CONTENT, content),
                    MessageValue.number(POSITION, position),
                    MessageValue.number(LENGTH, length)
            ));
        }

        @Override
        public Task httpCallStart(byte[] content, Long position, Long length) {
            return task(MessageName.HTTP_SIMPLE_CALL, send -> send.send(
                    MessageName.HTTP_SIMPLE_CALL,
                    MessageValue.blob(CONTENT, content),
                    MessageValue.number(POSITION, position),
                    MessageValue.number(LENGTH, length)
            ));
        }
    }



}
