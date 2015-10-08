package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.protocol.StandardProtocolImpl;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiver implements Receiver<StandardAddress> {

    private ReceiverCtrl<StandardAddress> receiverCtrl;
    private StandardAddress address;


    public StandardAddress getAddress() {return address;}

    public BasicReceiver(ReceiverCtrl<StandardAddress> receiverCtrl) {
        this.address = receiverCtrl.createNewAddress(this);
    }

    abstract protected void handle(MessageContextHandle messageContextHandle);

    public void receive(MessageContext context) {
        MessageContextHandle messageContextHandle = new MessageContextHandle(context);
        handle(messageContextHandle);
        if (!messageContextHandle.isHandled())
            messageContextHandle.createStandardProtocolSession().sendErrorTo("", StandardProtocol.ErrorKind.UnknownMessage);
    }

    protected class MessageContextHandle {

        MessageContext context;
        boolean handled = false;

        public boolean isHandled() {return handled;}
        public void setHandled() {handled = true;}

        public MessageContextHandle(MessageContext context) {
            this.context = context;
        }

        StandardProtocol.StandardSession createStandardProtocolSession() {
            return new StandardProtocolImpl(BasicReceiver.this, receiverCtrl).createSession(context.getSender());
        }


        public boolean messageHasName(Message.MessageName messageName) {
            return context.hasName(messageName);
        }


    }
}
