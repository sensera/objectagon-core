package org.objectagon.core.service;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.tasks.Task;

/**
 * Created by christian on 2015-10-08.
 */
public class AbstractService implements Service, Service.Commands {

    private Status status = Status.Stopped;
    private InternalServiceReceiver internalReceiver;
    private Task currentTask;

    public AbstractService(BasicReceiverCtrl receiverCtrl) {
        internalReceiver = new InternalServiceReceiver(this, receiverCtrl);
    }

    public void startService(final ServiceWorker serviceWorker) {

        if (status==Status.Starting) {
            currentTask.addCompletedListener(serviceWorker);
            return;
        }

        if (status==Status.Stopping) {
            serviceWorker.replyWithError(ServiceProtocol.ErrorKind.ServiceIsStoppning);
            return;
        }

        if (status==Status.Started) {
            serviceWorker.replyOk();
            return;
        }

        currentTask = createStartServiceTask(new Task.TaskCtrl() {
            public void success(Message message) {
                status = Status.Started;
                currentTask = null;

            }

            public void failed(Message message) {
                currentTask = null;
            }

            public StandardAddress createNewAddress(Receiver receiver) {
                return internalReceiver.createNewStandardAddress(receiver);
            }

            public void transport(Envelope envelope) {
                internalReceiver.transport(envelope);
            }
        });

        if (currentTask==null) {
            status = Status.Started;
            serviceWorker.replyOk();
            return;
        }

        currentTask.addCompletedListener(serviceWorker);

        currentTask.start();
    }

    public void stopService(ServiceWorker serviceWorker) {
        if (status==Status.Stopping) {
            currentTask.addCompletedListener(serviceWorker);
            return;
        }

        if (status==Status.Starting) {
            serviceWorker.replyWithError(ServiceProtocol.ErrorKind.ServiceIsStarting);
            return;
        }

        if (status==Status.Stopped) {
            serviceWorker.replyOk();
            return;
        }

        currentTask = createStopServiceTask(new Task.TaskCtrl() {
            public void success(Message message) {
                status = Status.Stopped;
                currentTask = null;
            }

            public void failed(Message message) {
                currentTask = null;
            }

            public StandardAddress createNewAddress(Receiver receiver) {
                return internalReceiver.createNewStandardAddress(receiver);
            }

            public void transport(Envelope envelope) {
                internalReceiver.transport(envelope);
            }
        });

        if (currentTask==null) {
            status = Status.Stopped;
            serviceWorker.replyOk();
            return;
        }

        currentTask.addCompletedListener(serviceWorker);

        currentTask.start();
    }

    protected StartServiceTask createStartServiceTask(Task.TaskCtrl taskCtrl) { return null; }
    protected StopServiceTask createStopServiceTask(Task.TaskCtrl taskCtrl) { return null; }


}
