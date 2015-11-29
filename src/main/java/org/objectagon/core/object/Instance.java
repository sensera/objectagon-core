package org.objectagon.core.object;

import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public interface Instance {


    enum TaskName implements Task.TaskName {
        SET_VALUE, GET_VALUE

    }
}
