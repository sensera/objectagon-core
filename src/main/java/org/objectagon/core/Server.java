package org.objectagon.core;

import org.objectagon.core.msg.*;

/**
 * Created by christian on 2015-10-06.
 */
public interface Server extends Transporter {

    ServerId getServerId();

    void registerFactory(Name name, Factory factory);

    <R extends Receiver> R createReceiver(Name name);

    void registerReceiver(Address address, Receiver receiver);

    interface Factory<R extends Receiver> {
        R create();
    }

    interface ServerId {}

    interface EnvelopeProcessor extends Transporter {

    }

}
