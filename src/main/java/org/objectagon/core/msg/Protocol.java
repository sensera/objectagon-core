package org.objectagon.core.msg;

import org.objectagon.core.exception.ErrorKind;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<U extends Protocol.Session> {
    ProtocolName getName();

    U createSession(Address target);

    interface ProtocolName extends Name {}

    interface Session {
        void replyWithError(org.objectagon.core.exception.ErrorKind errorKind);
    }

}
