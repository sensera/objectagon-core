package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Envelope {

    void unwrap(Envelope.Unwrapper unwrapper);

    interface Unwrapper {
        void message(Address sender, Message message);
    }
}
