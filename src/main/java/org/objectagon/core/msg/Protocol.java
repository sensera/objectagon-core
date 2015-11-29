package org.objectagon.core.msg;

import org.objectagon.core.exception.ErrorKind;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<U extends Protocol.Session> {
    ProtocolName getName();

    U createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner);

    interface ProtocolName extends Name {}

    interface Session {
        SessionId getSessionId();
        void replyWithError(org.objectagon.core.exception.ErrorKind errorKind);

    }

    interface SessionId {

        Message.Value toValue();
    }

    interface Factory {
        <U extends Protocol.Session> Protocol<U> create();
    }

    interface SessionFactory {
        <U extends Protocol.Session> U createSession(ProtocolName protocolName, Composer composer);
    }

    @FunctionalInterface
    interface SessionOwner {
        void invalidate(SessionId session);
    }
}
