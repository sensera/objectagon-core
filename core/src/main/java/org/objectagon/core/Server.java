package org.objectagon.core;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public interface Server extends Transporter {

    Message.Field SERVER_ID = NamedField.name("SERVER_ID");

    <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Receiver.Configurations... configurations);

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
        <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Receiver.Configurations... configurations);
    }

    @FunctionalInterface
    interface RegisterFactory {
        void registerFactory(Name name, Server.Factory factory);
    }

    @FunctionalInterface
    interface SystemTime {
        long currentTimeMillis();
    }

    interface AliasCtrl {
        void registerAliasForAddress(Name name, Address address);
        void removeAlias(Name name);
        Optional<Address> lookupAddressByAlias(Name name);
    }

}
