package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.ReceiverCtrlIdName;
import org.objectagon.core.service.name.NameServiceImpl;

/**
 * Created by christian on 2015-11-22.
 */
public class StandardFactory<R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId> extends AbstractFactory<R, A, C> implements Receiver.ReceiverCtrl<R,A>{

    public static
    <R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId> StandardFactory<R,A,C>
            create(Server server,
                   C ctrlId,
                   Receiver.CreateNewAddress<R, A, C> createNewAddress,
                   CreateReceiver<StandardFactory<R,A,C>, R, A> factory) {
        return new StandardFactory<>(server, ctrlId, createNewAddress, factory);
    }

    private CreateReceiver<StandardFactory<R,A,C>, R, A> factory;

    public StandardFactory(Server server, C ctrlId, Receiver.CreateNewAddress<R, A, C> createNewAddress, CreateReceiver<StandardFactory<R,A,C>, R, A> factory) {
        super(server, ctrlId, createNewAddress);
        this.factory = factory;
    }

    @Override
    public R create() {
        return factory.create(this);
    }

    @FunctionalInterface
    public interface CreateReceiver<K extends Receiver.ReceiverCtrl<R,A>, R extends Receiver<A>, A extends Address> {
        R create(K receiverCtrl);
    }
}
