package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-11-08.
 */
public class SequenceTask extends AbstractTask {

    private List<Task> sequence = new LinkedList<>();
    private int atSequence = 0;

    public SequenceTask(TaskCtrl taskCtrl, TaskName name) {
        super(taskCtrl, name);
    }

    @Override
    protected void internalStart() {
        activeTask().start();
    }

    private Task activeTask() {
        return sequence.get(atSequence);
    }

    public void add(Task task) {
        sequence.add(task);
        task.addSuccessAction(taskWorker -> startNextTask());
        task.addFailedAction(taskWorker -> failed(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED));
    }

    private void startNextTask() {
        atSequence++;
        activeTask().start();
    }
}
