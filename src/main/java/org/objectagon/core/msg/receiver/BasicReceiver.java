package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.BasicAddress;
import org.objectagon.core.msg.message.BasicMessage;
import org.objectagon.core.msg.protocol.BasicProtocol;
import org.objectagon.core.msg.protocol.BasicProtocolImpl;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiver implements Receiver<BasicAddress> {

    private ReceiverCtrl<BasicAddress> receiverCtrl;
    private BasicAddress address;


    public BasicAddress getAddress() {return address;}

    public BasicReceiver(ReceiverCtrl<BasicAddress> receiverCtrl) {
        this.address = receiverCtrl.createNewAddress(this);
    }

    abstract protected void handle(MessageContextHandle messageContextHandle);

    public void receive(MessageContext context) {
        MessageContextHandle messageContextHandle = new MessageContextHandle(context);
        handle(messageContextHandle);
    }

    private class MessageContextHandle {

        MessageContext context;

        public MessageContextHandle(MessageContext context) {
            this.context = context;
        }

        BasicProtocol createBasicProtocol() {
            return new BasicProtocolImpl(receiverCtrl, context.getSender());
        }
    }
}
