package org.objectagon.core.msg;

import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public interface Envelope {

    void unwrap(Envelope.Unwrapper unwrapper);
    void targets(Envelope.Targets targets);

    Message.Values headers();

    interface Unwrapper {
        void message(Address sender, Message message);
    }

    interface Targets {
         Optional<Transporter> target(Address address);
    }
}
