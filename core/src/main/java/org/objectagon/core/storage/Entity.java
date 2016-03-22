package org.objectagon.core.storage;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-15.
 */
public interface Entity<I extends Identity, D extends Data> extends Receiver<I> {

    enum TaskName implements Task.TaskName {
        COMMIT_DATA_VERSION, ROLLBACK_DATA_VERSION, DELETE_DATA_VERSION_FROM_PERSISTANCE

    }

    interface EntityConfig extends SetInitialValues {
        DataVersion getDataVersion();
        long getDataVersionCounter();
    }
}
