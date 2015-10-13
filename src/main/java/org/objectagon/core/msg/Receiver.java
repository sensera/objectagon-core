package org.objectagon.core.msg;

import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<U extends Address> {
    U getAddress();
    void receive(Envelope envelope);

    interface ReceiverCtrl<U> extends Transporter {
        U createNewAddress(Receiver receiver);
    }

    interface Worker {
        boolean isHandled();

        boolean messageHasName(Message.MessageName messageName);

        void setHandled();

        void replyOk();

        void replyWithError(StandardProtocol.ErrorKind errorKind);
    }

    interface WorkerContext {

        Composer createStandardComposer();

        Transporter getTransporter();

        Address getSender();

        boolean hasName(Message.MessageName messageName);

    }
}
