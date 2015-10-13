package org.objectagon.core.service;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.tasks.Task;

/**
 * Created by christian on 2015-10-08.
 */
public class AbstractService<W extends Service.ServiceWorker> extends BasicReceiverImpl<StandardAddress, BasicReceiverCtrl<StandardAddress>, W> implements Service {

    private Status status = Status.Stopped;
    private Task currentTask;

    public AbstractService(BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    public void startService(final W serviceWorker) {

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
                return getReceiverCtrl().createNewAddress(receiver);
            }

            public void transport(Envelope envelope) {
                getReceiverCtrl().transport(envelope);
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

    public void stopService(final W serviceWorker) {
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
                return getReceiverCtrl().createNewAddress(receiver);
            }

            public void transport(Envelope envelope) {
                getReceiverCtrl().transport(envelope);
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

    @Override
    protected void handle(W serviceWorker) {
        if (serviceWorker.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            startService(serviceWorker);
            serviceWorker.setHandled();
        } else if (serviceWorker.messageHasName(ServiceProtocol.MessageName.START_SERVICE)) {
            stopService(serviceWorker);
            serviceWorker.setHandled();
        }
    }

    @Override
    protected W createWorker(WorkerContext workerContext) {
        return (W) new ServiceWorkerImpl(workerContext);
    }



}
