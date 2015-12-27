package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.StandardReceiverCtrlImpl;

/**
 * Created by christian on 2015-11-22.
 */
public abstract class AbstractFactory<C extends Receiver.CtrlId, A extends Address, P extends Receiver.CreateNewAddressParams> extends StandardReceiverCtrlImpl<P> implements Server.Factory {

    private long counter = 0;
    private C ctrlId;
    private Receiver.CreateNewAddress<C,A> createNewAddress;

    @Override public Receiver.CtrlId getCtrlId() {return ctrlId;}

    public AbstractFactory(Server.Ctrl serverCtrl, C ctrlId, Receiver.CreateNewAddress<C,A> createNewAddress) {
        super(serverCtrl);
        this.ctrlId = ctrlId;
        this.createNewAddress = createNewAddress;
    }

    @Override
    protected <U extends Address> U internalCreateAddress(Long addressId, P params) {
        return (U) createNewAddress.createNewAddress(getServerId(), ctrlId, addressId);
    }
}
