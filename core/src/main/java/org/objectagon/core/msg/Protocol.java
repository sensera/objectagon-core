package org.objectagon.core.msg;

import org.objectagon.core.msg.protocol.ProtocolNameStandardAddress;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<S extends Protocol.Send, R extends Protocol.Reply> extends Receiver<Protocol.ProtocolAddress> {

    S createSend(CreateSendParam createSend);
    R createReply(CreateReplyParam createReply);

    interface ProtocolName extends Name {
        String getName();
    }

    interface ProtocolAddress extends Address {
        ProtocolNameStandardAddress create(long timestamp, long addressId);
    }

    interface SenderAddress extends Address {}

    interface CreateSendParam {
        Composer getComposer();
    }
    interface CreateReplyParam {
        Composer getComposer();
    }

    interface Session {
        void terminate();
    }

    interface Send extends Session {}

    interface Reply extends Session {
        void replyOk();
        void replyWithParam(Message.Value param);
        void replyWithParam(Iterable<Message.Value> param);
    }

    @FunctionalInterface
    interface CreateSend<S extends Send>{
        S createSend(CreateSendParam createSend);
    }

    @FunctionalInterface
    interface CreateReply<R extends Reply> {
        R createReply(CreateReplyParam createReply);
    }

}
