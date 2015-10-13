package org.objectagon.core.service;

import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;

/**
 * Created by christian on 2015-10-13.
 */
class InternalServiceReceiver extends BasicReceiverImpl<StandardAddress, BasicReceiverCtrl<StandardAddress>, Service.ServiceWorker> {

    private Service.Commands commands;

    public InternalServiceReceiver(Service.Commands commands, BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        this.commands = commands;
    }

    @Override
    protected void handle(Service.ServiceWorker serviceWorker) {
        if (serviceWorker.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            commands.startService(serviceWorker);
            serviceWorker.setHandled();
        } else if (serviceWorker.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            commands.stopService(serviceWorker);
            serviceWorker.setHandled();
        }
    }

    @Override
    protected Service.ServiceWorker createWorker(WorkerContext workerContext) {
        return new ServiceWorkerImpl(workerContext);
    }

    public StandardAddress createNewStandardAddress(Receiver receiver) {
        return getReceiverCtrl().createNewAddress(receiver);
    }


    public void transport(Envelope envelope) {
        getReceiverCtrl().transport(envelope);
    }
}
