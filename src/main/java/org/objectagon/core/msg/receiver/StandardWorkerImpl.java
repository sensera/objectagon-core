package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-09.
 */
public class StandardWorkerImpl extends BasicWorkerImpl implements StandardWorker {

    public StandardWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }

    @Override
    public Message.MessageName getMessageName() {return getWorkerContext().getMessageName();}
}
