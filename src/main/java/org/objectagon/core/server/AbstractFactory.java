package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.ReceiverCtrlIdName;
import org.objectagon.core.msg.receiver.StandardReceiverCtrlImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;

/**
 * Created by christian on 2015-11-22.
 */
public abstract class AbstractFactory<R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId> extends StandardReceiverCtrlImpl<R,A> implements Server.Factory<R> {

    private long counter = 0;
    private C ctrlId;
    private Receiver.CreateNewAddress<R,A,C> createNewAddress;

    public AbstractFactory(Server server, C ctrlId, Receiver.CreateNewAddress<R,A,C> createNewAddress) {
        super(server);
        this.ctrlId = ctrlId;
        this.createNewAddress = createNewAddress;
    }

    @Override
    public A createNewAddress(R receiver) {
        long id;
        synchronized (this) {
            id = counter++;
        }
        A address = createNewAddress.createNewAddress(getServerId(), ctrlId, id);
        registerReceiver(address, receiver);
        return address;
    }


}
