package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Message<P extends Message.Payload> {
    String getName();
    P getPayload();

    interface Payload {}
}
