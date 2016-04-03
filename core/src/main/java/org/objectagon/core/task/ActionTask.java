package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.utils.FindNamedConfiguration;

/**
 * Created by christian on 2015-11-03.
 */
public class ActionTask extends AbstractTask {
    private TaskBuilder.Action action;

    public ActionTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, TaskBuilder.Action action) {
        super(taskCtrl, taskName);
        this.action = action;
    }

    @Override
    protected void internalStart() {
        action.run();
    }

    @Override
    protected void handle(TaskWorker worker) {

    }

    @Override
    protected Address createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardAddress::standard);
    }
}
