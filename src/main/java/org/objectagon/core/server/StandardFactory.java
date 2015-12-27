package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.ReceiverCtrlIdName;
import org.objectagon.core.service.name.NameServiceImpl;

/**
 * Created by christian on 2015-11-22.
 */
public class StandardFactory<R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId, P extends Receiver.CreateNewAddressParams> extends AbstractFactory<C,A, P> implements Receiver.ReceiverCtrl<P>{

    public static
    <R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId, P extends Receiver.CreateNewAddressParams> StandardFactory<R,A,C,P>
            create(Server.Ctrl serverCtrl,
                   C ctrlId,
                   Receiver.CreateNewAddress<C,A> createNewAddress,
                   CreateReceiver<P, StandardFactory<R,A,C,P>,R,A> factory) {
        return new StandardFactory<>(serverCtrl, ctrlId, createNewAddress, factory);
    }

    private CreateReceiver<P, StandardFactory<R,A,C,P>, R, A> factory;

    public StandardFactory(Server.Ctrl serverCtrl, C ctrlId, Receiver.CreateNewAddress<C,A> createNewAddress, CreateReceiver<P,StandardFactory<R,A,C,P>,R,A> factory) {
        super(serverCtrl, ctrlId, createNewAddress);
        this.factory = factory;
    }

    @Override
    public R create() {
        return factory.create(this);
    }

    @FunctionalInterface
    public interface CreateReceiver<P extends Receiver.CreateNewAddressParams, K extends Receiver.ReceiverCtrl<P>, R extends Receiver<A>, A extends Address> {
        R create(K receiverCtrl);
    }
}
