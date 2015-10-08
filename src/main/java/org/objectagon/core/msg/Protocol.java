package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol<U extends Protocol.Session> {
    String getName();

    U createSession(Address target);

    interface Session {

    }
}
