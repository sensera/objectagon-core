package org.objectagon.core.task;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.receiver.BasicWorker;

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
    protected Address createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return StandardAddress.standard(serverId, timestamp, id);
    }
}
