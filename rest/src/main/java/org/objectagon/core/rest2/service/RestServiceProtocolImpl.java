package org.objectagon.core.rest2.service;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;

/**
 * Created by christian on 2017-01-22.
 */
public class RestServiceProtocolImpl extends AbstractProtocol<RestServiceProtocol.Send, Protocol.Reply> implements RestServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(REST_SERVICE_PROTOCOL, new SingletonFactory<>(RestServiceProtocolImpl::new));
    }
    public RestServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, REST_SERVICE_PROTOCOL);
    }

    @Override
    public RestServiceProtocol.Send createSend(CreateSendParam createSend) {
        return null;
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return null;
    }

}
