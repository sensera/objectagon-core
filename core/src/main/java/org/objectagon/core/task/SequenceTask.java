package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by christian on 2015-11-08.
 */
public class SequenceTask extends AbstractTask {

    private List<TaskSupplier> sequence = new LinkedList<>();
    private List<Message.Value> values = new ArrayList<>();

    SequenceTask(Receiver.ReceiverCtrl taskCtrl, TaskName name) {
        super(taskCtrl, name);
    }

    @Override
    protected void internalStart() {
        if (trace) System.out.println("SequenceTask.internalStart Count tasks "+sequence.size());
        startNextTask(Task.MessageName.COMPLETED, Message.NO_VALUES);
    }

    private Optional<Task> activeTask(Message.MessageName messageName, Iterable<Message.Value> values) {
        try {
            if (sequence.isEmpty())
                return Optional.empty();
            Optional<Task> selectedTask = sequence.remove(0).createTask(messageName, values);
            selectedTask.ifPresent(task -> {
                task.addSuccessAction(this::startNextTask);
                task.addFailedAction(this::failed);
            });
            if (!selectedTask.isPresent())
                return activeTask(messageName, values);
            return selectedTask;
        } catch (IndexOutOfBoundsException e) {
            throw new SevereError(ErrorClass.TASK, ErrorKind.INCONSISTENCY, MessageValue.name(getName()));
        }
    }

    public SequenceTask add(Task task) {
        if (task == null)
            throw new NullPointerException("task is null!");
        sequence.add((m,v) -> Optional.of(task));
        return this;
    }

    public SequenceTask add(TaskSupplier taskSupplier) {
        if (taskSupplier == null)
            throw new NullPointerException("task is null!");
        sequence.add(taskSupplier);
        return this;
    }

    private void startNextTask(Message.MessageName messageName, Iterable<Message.Value> values) {
        Optional<Task> task = activeTask(messageName, values);
        if (!task.isPresent()) {
            if (trace) System.out.println("SequenceTask.startNextTask COMPLETED task");
            success(Task.MessageName.COMPLETED, values);
            return;
        }
        task.ifPresent(t -> {
            if (trace) System.out.println("SequenceTask.startNextTask #"+t.getName());
            if (trace) t.trace();
            t.start();
        });
    }


    @Override
    protected void handle(TaskWorker worker) {

    }

    public void add(Message.Value value) { values.add(value); }

    @FunctionalInterface
    public interface TaskSupplier {
        Optional<Task> createTask(Message.MessageName messageName, Iterable<Message.Value> values);
    }

}
