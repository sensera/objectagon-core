package org.objectagon.core.msg;

import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public interface Envelope {

    void unwrap(Envelope.Unwrapper unwrapper);
    void Targets(Envelope.Targets targets);

    interface Unwrapper {
        void message(Address sender, Message message);
    }

    interface Targets {
        <A extends Address> Optional<Transporter> target(A address);
    }
}
