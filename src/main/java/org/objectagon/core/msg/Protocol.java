package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorKind;
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
    }

    interface SessionId extends Address, CtrlId {
        Message.Value toValue();
    }

    interface Factory<U extends Protocol.Session> {
        Protocol<U> create(Server.ServerId serverId);
    }

    interface SessionFactory {
        <U extends Protocol.Session> U createSession(ProtocolName protocolName, Composer composer);
    }

    interface SessionOwner {
        Transporter getTransporter();
        Composer getComposer();
        void invalidate(SessionId session);
    }
}
