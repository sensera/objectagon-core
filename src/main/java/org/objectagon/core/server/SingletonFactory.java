package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.msg.receiver.StandardReceiverCtrlImpl;

/**
 * Created by christian on 2015-11-22.
 */
public class SingletonFactory<C extends Receiver.CtrlId, A extends Address> extends StandardReceiverCtrlImpl<Receiver.CreateNewAddressParams> implements Server.Factory {

    private A address;
    private C ctrlId;
    private CreateReceiver createReceiver;

    @Override public Receiver.CtrlId getCtrlId() {return ctrlId;}

    public SingletonFactory(Server.Ctrl serverCtrl, C ctrlId, A address, CreateReceiver createReceiver) {
        super(serverCtrl);
        this.ctrlId = ctrlId;
        this.address = address;
        this.createReceiver = createReceiver;
    }

    @Override
    protected <U extends Address> U internalCreateAddress(Long addressId, Receiver.CreateNewAddressParams params) {
        return (U) address;
    }

    @Override
    public <R extends Receiver> R create() {
        return (R) createReceiver.create(this);
    }

    @FunctionalInterface
    public interface CreateReceiver<R extends Receiver<A>, A extends Address> {
        R create(StandardReceiverCtrl<Receiver.CreateNewAddressParams> receiverCtrl);
    }

}
