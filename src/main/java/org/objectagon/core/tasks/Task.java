package org.objectagon.core.tasks;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;

/**
 * Created by christian on 2015-10-11.
 */
public interface Task extends BasicReceiver {

    enum Status {
        Unstarted, Initializing, Started, Verifying, Completed;
    }

    TaskName getName();

    void start();
    void addCompletedListener(CompletedListener completedListener);

    interface TaskCtrl extends BasicReceiverCtrl {
        void success(Message message);
        void failed(Message message);
    }

    interface TaskName extends Name {

    }

    interface CompletedListener {
        void success();
        void failed(StandardProtocol.ErrorKind errorKind, String description);
    }

}
