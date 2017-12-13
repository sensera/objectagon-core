package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-15.
 */
public interface Entity<I extends Identity, D extends Data> extends Receiver<I> {

    Name ENTITY_CONFIG_NAME = StandardName.name("ENTITY_CONFIG_NAME");

    <V extends Version> D createNewDataWithVersion(V versionForNewData);

    enum TaskName implements Task.TaskName {
        COMMIT_DATA_VERSION, ROLLBACK_DATA_VERSION, DELETE_DATA_VERSION_FROM_PERSISTANCE

    }

    interface EntityConfig extends NamedConfiguration {
        <I extends Identity, V extends Version> DataRevision<I,V> getDataVersion(I identity);
        long getDataVersionCounter();
        Message.Values initialParams();
    }
}
