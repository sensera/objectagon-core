package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<U extends Address> {
    U getAddress();
    void receive(MessageContext context);

    interface MessageContext {
        Address getSender();
        Message getMessage();
        boolean hasName(Message.MessageName messageName);
    }

    interface ReceiverCtrl<U> extends Transporter {
        U createNewAddress(Receiver receiver);
    }
}
