package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.name.NameServiceProtocol;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<U extends Address> {
    U getAddress();
    void receive(Envelope envelope);

    interface ReceiverCtrl<R extends Receiver<U>, U extends Address> extends Transporter {
        U createNewAddress(R receiver);
    }

    interface Worker {
        boolean isHandled();

        boolean messageHasName(Message.MessageName messageName);

        void setHandled();

        void replyOk();

        void replyWithError(StandardProtocol.ErrorKind errorKind);

        void replyWithParam(Message.Value param);

        void replyWithParam(Message message);

        Message.Value getValue(Message.Field field);
    }

    interface WorkerContext {

        Composer createStandardComposer();

        Transporter getTransporter();

        Address getSender();

        boolean hasName(Message.MessageName messageName);

        Message.Value getValue(Message.Field field);

        Message.MessageName getMessageName();
    }

    public interface CtrlId extends Name {}

    @FunctionalInterface
    interface CreateNewAddress<R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId> {
        A createNewAddress(Server.ServerId serverId, C ctrlId, long id);
    }

}
