package org.objectagon.core.service;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardWorker;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;

/**
 * Created by christian on 2015-10-08.
 */
public interface Service {

    enum Status {
        Stopped, Starting, Started, Stopping;
    }

    interface ServiceActionCommands<W extends ServiceWorker> extends Reactor.ActionInitializer {
        boolean isStarting();
        boolean isStopping();
        boolean isStarted();
        boolean isStopped();

        void addCompletedListener(W serviceWorker);

        void setStartedStatusAndClearCurrentTask();

        void setStoppedStatusAndClearCurrentTask();

        void transport(Envelope envelope);

        Optional<Task> createStartServiceTask(W serviceWorker);

        Optional<Task> createStopServiceTask(W serviceWorker);
    }

    interface ServiceWorker extends StandardWorker, Task.CompletedListener {
        void replyWithError(ServiceProtocol.ErrorKind errorKind);
        TaskBuilder getTaskBuilder();
    }

}
