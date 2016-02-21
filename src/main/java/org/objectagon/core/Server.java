package org.objectagon.core;

import org.objectagon.core.msg.*;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-10-06.
 */
public interface Server extends Transporter {

    <R extends Receiver> R createReceiver(Name name, Receiver.Initializer initializer);

    void transport(Envelope envelope);
    void registerFactory(Name name, Factory factory);

    TaskBuilder getTaskBuilder();

    interface Factory<R extends Receiver> {
        R create(Receiver.ReceiverCtrl receiverCtrl);
    }

    ServerId getServerId();

    interface ServerId extends Name {}

    interface EnvelopeProcessor extends Transporter {}

    interface CreateReceiverByName {
        <R extends Receiver> R createReceiver(Name name, Receiver.Initializer initializer);
    }

    @FunctionalInterface
    interface RegisterFactory {
        void registerFactory(Name name, Server.Factory factory);
    }

    @FunctionalInterface
    interface SystemTime {
        long currentTimeMillis();
    }


}
