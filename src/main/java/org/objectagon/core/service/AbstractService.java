package org.objectagon.core.service;

import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiver;

/**
 * Created by christian on 2015-10-08.
 */
public class AbstractService extends BasicReceiver implements Service {

    public AbstractService(ReceiverCtrl<StandardAddress> receiverCtrl) {
        super(receiverCtrl);
    }

    public void start() {

    }

    public void stop() {

    }

    private void startService(MessageContextHandle messageContextHandle) {

    }

    private void stopService(MessageContextHandle messageContextHandle) {

    }

    @Override
    protected void handle(BasicReceiver.MessageContextHandle messageContextHandle) {
        if (messageContextHandle.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            startService(messageContextHandle);
            messageContextHandle.setHandled();
        } else if (messageContextHandle.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            stopService(messageContextHandle);
        }
    }




}
