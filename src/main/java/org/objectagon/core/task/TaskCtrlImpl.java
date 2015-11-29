package org.objectagon.core.task;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiverCtrlImpl;

/**
 * Created by christian on 2015-11-23.
 */
public class TaskCtrlImpl extends BasicReceiverCtrlImpl<Task, Address>  implements Task.TaskCtrl {

    public TaskCtrlImpl(Transporter transporter, Protocol.SessionFactory sessionFactory, Server.RegisterReceiver registerReceiver, Server.ServerId serverId, Receiver.CtrlId ctrlId) {
        super(transporter, sessionFactory, registerReceiver, serverId, ctrlId);
    }

    @Override
    protected Address internalCreateNewAddress(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId) {
        return StandardAddress.standard(serverId, ctrlId, addressId);
    }

}
