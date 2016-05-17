package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Receiver;

import java.util.Optional;

/**
 * Created by christian on 2016-03-22.
 */
public class SingletonFactory<R extends Receiver> implements Server.Factory<R> {
    private final Server.Factory<R> factory;
    private Optional<R> receiver = Optional.empty();

    public SingletonFactory(Server.Factory<R> factory) {
        this.factory = factory;
    }

    @Override
    public R create(Receiver.ReceiverCtrl receiverCtrl) {
        return receiver.orElseGet(() -> {
            R value = factory.create(receiverCtrl);
            receiver = Optional.of(value);
            return value;
        });
    }
}
