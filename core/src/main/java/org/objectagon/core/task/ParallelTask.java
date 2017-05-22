package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.objectagon.core.msg.protocol.StandardProtocol.MessageName.OK_MESSAGE;

/**
 * Created by christian on 2015-11-08.
 */
public class ParallelTask extends AbstractTask {

    private final List<TaskSupplier> tasks = new LinkedList<>();
    private final List<Message.Value> values = new ArrayList<>();
    private int taskCount = 0;
    private boolean failed = false;

    ParallelTask(ReceiverCtrl taskCtrl, TaskName name) {
        super(taskCtrl, name);
    }

    @Override
    protected void internalStart() {
        if (trace) System.out.println("ParallelTask.internalStart Count tasks "+ tasks.size());
        final List<Task> activeTasks;
        synchronized (tasks) {
            activeTasks = tasks.stream()
                    .map(TaskSupplier::createTask)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(task -> task.addSuccessAction(this::subTaskSuccess))
                    .peek(task -> task.addFailedAction(this::subTaskFailed))
                    .collect(Collectors.toList());

            taskCount = activeTasks.size();
            if (trace) System.out.println("ParallelTask.internalStart Count started tasks " + taskCount);
        }

        activeTasks
                .forEach(Task::start);
    }

    public ParallelTask add(Task task) {
        if (task == null)
            throw new NullPointerException("task is null!");
        if (isCompleted())
            throw new NullPointerException("Parallel task is completed!");
        synchronized (tasks) {
            if (taskCount > 0) {
                if (trace) System.out.println("ParallelTask.add start "+task.getName());
                taskCount++;
                task
                        .addSuccessAction(this::subTaskSuccess)
                        .addFailedAction(this::subTaskFailed)
                        .start();
            } else {
                if (trace) System.out.println("ParallelTask.add queue "+task.getName());
                tasks.add(() -> Optional.of(task));
            }
        }
        return this;
    }

    public ParallelTask add(TaskSupplier taskSupplier) {
        if (taskSupplier == null)
            throw new NullPointerException("task is null!");
        add(taskSupplier.createTask().orElseGet(() -> null));
        return this;
    }

    private boolean subtractTaskCount() {
        synchronized (tasks) {
            taskCount--;
            return taskCount == 0;
        }
    }

    private void subTaskSuccess(Message.MessageName messageName, Iterable<Message.Value> values) {
        boolean completed = subtractTaskCount();
        this.values.add(MessageValue.message(messageName, values));
        if (completed)
            allTasksHasCompleted();
    }

    private void subTaskFailed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        boolean completed = subtractTaskCount();
        failed = true;
        this.values.add(MessageValue.error(errorClass, errorKind, values));
        if (completed)
            allTasksHasCompleted();
    }

    private void allTasksHasCompleted() {
        if (failed) {
            if (trace) System.out.println("ParallelTask.allTasksHasCompleted failed");
            failed(ErrorClass.TASK, ErrorKind.UNKNOWN, values);
        } else {
            if (trace) System.out.println("ParallelTask.allTasksHasCompleted completed");
            success(OK_MESSAGE, values);
        }
    }

    @Override
    protected void handle(TaskWorker worker) {

    }

    public void add(Message.Value value) { values.add(value); }

    @FunctionalInterface
    public interface TaskSupplier {
        Optional<Task> createTask();
    }

}
