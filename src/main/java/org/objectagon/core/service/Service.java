package org.objectagon.core.service;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.tasks.Task;

/**
 * Created by christian on 2015-10-08.
 */
public interface Service {

    enum Status {
        Stopped, Starting, Started, Stopping;
    }

    interface ServiceWorker extends BasicWorker, Task.CompletedListener {
        void replyWithError(ServiceProtocol.ErrorKind errorKind);
    }

    interface StartServiceTask extends Task {}
    interface StopServiceTask extends Task {}
}
