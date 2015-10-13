package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<U extends Protocol.Session> {
    ProtocolName getName();

    U createSession(Address target);

    U createSession(Composer composer, Address target);

    interface ProtocolName extends Name {}

    interface Session {}

}
