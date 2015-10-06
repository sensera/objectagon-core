package org.objectagon.core.msg;

import org.objectagon.core.msg.message.BasicMessage;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<U extends Address> {
    U getAddress();
    void receive(MessageContext context);

    interface MessageContext {
        Address getSender();
        Message getMessage();
    }

    interface ReceiverCtrl<U> extends Protocol.Send {
        U createNewAddress(Receiver receiver);
    }
}
