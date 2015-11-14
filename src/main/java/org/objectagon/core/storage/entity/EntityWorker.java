package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-11-01.
 */
public interface EntityWorker extends BasicWorker {
    PersistenceServiceProtocol createPersistenceServiceProtocol();

    TaskBuilder getTaskBuilder();
}
