package org.objectagon.core;

import org.objectagon.core.msg.*;

/**
 * Created by christian on 2015-10-06.
 */
public interface Server extends Transporter {

    void registerFactory(Name name, Factory factory);

    interface Factory<R extends Receiver> {
        R create();
    }

    interface ServerId {}

    interface EnvelopeProcessor extends Transporter {

    }

    interface RegisterReceiver {
        void registerReceiver(Address address, Receiver receiver);
    }

    interface Ctrl extends RegisterReceiver {
        ServerId getServerId();
        void transport(Envelope envelope);
    }

}
