package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<U extends Protocol.Session> extends Receiver<Protocol.ProtocolName>{
    ProtocolName getName();

    U createSession(SessionOwner sessionOwner);

    interface ProtocolName extends Name, Address {}

    interface Session extends Receiver<SessionId> {
        SessionId getSessionId();
        void replyWithError(org.objectagon.core.exception.ErrorKind errorKind);
        void terminate();
    }

    interface SessionId extends Address, CtrlId {
        Message.Value toValue();
    }

    interface Factory<U extends Protocol.Session> {
        Protocol<U> create(Server.ServerId serverId);
    }

    interface SessionFactory {
        <U extends Protocol.Session> U createSession(ProtocolName protocolName, Composer composer);
        <S extends Session> FuncReply session(ProtocolName protocolName, Composer composer, Func<S> func);
    }

    interface SessionOwner {
        Transporter getTransporter();
        Composer getComposer();
        void registerReceiver(Address address, Receiver receiver);
        void terminated(SessionId session);

        <S extends Session> S createSession(ProtocolName protocolName);
    }

    @FunctionalInterface
    interface Func<S extends Session> {
        FuncReply run(S session);
    }

    interface FuncReply {
        Message.MessageName name();
        Iterable<Message.Value> values();
    }
}
