package org.objectagon.core.rest2.service;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.List;

/**
 * Created by christian on 2017-01-22.
 */
public class RestServiceProtocolImpl extends AbstractProtocol<RestServiceProtocol.Send, Protocol.Reply> implements RestServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(REST_SERVICE_PROTOCOL, new SingletonFactory<>(RestServiceProtocolImpl::new));
    }
    public RestServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, REST_SERVICE_PROTOCOL);
        createSend = RestServiceProtocolSend::new;
    }

    @Override
    public SimplifiedSend createSimplifiedSend(CreateSendParam createSend) {
        return new RestServiceProtocolSimplifiedSend(createSend);
    }

    private class RestServiceProtocolSend extends AbstractProtocolSend implements RestServiceProtocol.Send {

        public RestServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task restRequest(Method method, Name path, List<KeyValue<ParamName, Message.Value>> params) {
            return task(MessageName.REST_REQUEST, send -> {
                send.send(
                        MessageName.REST_REQUEST,
                        MessageValue.name(RestServiceProtocol.METHOD_FIELD, method),
                        MessageValue.name(RestServiceProtocol.PATH_FIELD, path),
                        MessageValue.map(RestServiceProtocol.PARAMS_FIELD, KeyValueUtil.toMap(params))
                );
            });
        }

        @Override
        public Task restContent(long sequence, byte[] bytes) {
            return task(MessageName.REST_CONTENT, send -> send.send(
                    MessageName.REST_CONTENT,
                    MessageValue.number(RestServiceProtocol.CONTENT_SEQUENCE, sequence),
                    MessageValue.blob(RestServiceProtocol.CONTENT_BYTES, bytes)
            ));
        }

        @Override
        public Task completed() {
            return task(MessageName.COMPLETED, send -> send.send(MessageName.COMPLETED))
                    .addSuccessAction((messageName, values) -> terminate())
                    .addFailedAction((errorClass, errorKind, values) -> terminate());
        }

        @Override
        public Task exceptionCaught(Throwable cause) {
            return task(MessageName.EXCEPTION_CAUGHT, send -> send.send(MessageName.EXCEPTION_CAUGHT))
                    .addSuccessAction((messageName, values) -> terminate())
                    .addFailedAction((errorClass, errorKind, values) -> terminate());
        }
    }

    private class RestServiceProtocolSimplifiedSend extends AbstractProtocolSend implements RestServiceProtocol.SimplifiedSend {
        public RestServiceProtocolSimplifiedSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task restRequest(Method method, Name path, String content, List<KeyValue<ParamName, Message.Value>> params) {
            return task(MessageName.SIMPLE_REST_CONTENT, send -> {
                send.send(
                        MessageName.SIMPLE_REST_CONTENT,
                        MessageValue.name(RestServiceProtocol.METHOD_FIELD, method),
                        MessageValue.name(RestServiceProtocol.PATH_FIELD, path),
                        MessageValue.text(RestServiceProtocol.CONTENT_FIELD, content),
                        MessageValue.map(RestServiceProtocol.PARAMS_FIELD, KeyValueUtil.toMap(params))
                );
            });
        }
    }
}
