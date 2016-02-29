package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public interface Instance extends Receiver<Instance.InstanceIdentity> {


    enum TaskName implements Message.MessageName, Task.TaskName {
        SET_VALUE, GET_VALUE

    }

    interface InstanceIdentity extends Identity {

    }
}
