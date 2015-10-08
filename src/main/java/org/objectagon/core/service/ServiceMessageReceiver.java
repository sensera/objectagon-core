package org.objectagon.core.service;

import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiver;

/**
 * Created by christian on 2015-10-08.
 */
public class ServiceMessageReceiver extends BasicReceiver {

    private final Service service;

    public ServiceMessageReceiver(Service service, ReceiverCtrl<StandardAddress> receiverCtrl) {
        super(receiverCtrl);
        this.service = service;
    }

    private void startService(MessageContextHandle messageContextHandle) {
        try {
            service.start();
        } catch (FailedToStartServiceException e) {
            e.printStackTrace();
        }
    }

    private void stopService(MessageContextHandle messageContextHandle) {
        try {
            service.stop();
        } catch (FailedToStopServiceException e) {
            e.printStackTrace();
        }
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
