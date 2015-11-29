package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;

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
        startNextTask(Task.MessageName.COMPLETED, Message.NO_VALUES);
    }

    private Task activeTask() {
        return sequence.get(atSequence);
    }

    public void add(Task task) {
        sequence.add(task);
        task.addSuccessAction(this::startNextTask);
        task.addFailedAction(this::failed);
    }

    private void startNextTask(Message.MessageName messageName, Iterable<Message.Value> values) {
        if (sequence.size()==atSequence) {
            success(Task.MessageName.COMPLETED, Message.NO_VALUES);
        }
        atSequence++;
        activeTask().start();
    }
}
