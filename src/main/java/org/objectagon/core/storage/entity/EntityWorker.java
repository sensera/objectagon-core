package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.object.ObjectVersion;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-11-01.
 */
public interface EntityWorker extends BasicWorker {
    PersistenceServiceProtocol createPersistenceServiceProtocol();

    ObjectVersion getVersion();

    TaskBuilder getTaskBuilder();

}
