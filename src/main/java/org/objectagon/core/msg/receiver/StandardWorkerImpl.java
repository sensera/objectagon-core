package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-11-09.
 */
public class StandardWorkerImpl extends BasicWorkerImpl implements StandardWorker {

    public StandardWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }
}
