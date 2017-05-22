package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

import java.util.Collections;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;

/**
 * Created by christian on 2017-04-14.
 */
public class BatchUpdateImpl implements BatchUpdate {

    private enum BatchTaskName implements Task.TaskName, Message.MessageName {
        CREATE_PLAN,
    }

    private TaskBuilder taskBuilder;
    private Targets targets;

    public BatchUpdateImpl(TaskBuilder taskBuilder, Targets targets) {
        this.taskBuilder = taskBuilder;
        this.targets = targets;
    }

    @Override
    public Task createPlan(Consumer<AddBasis> basisConsumer) {
        return taskBuilder.action(BatchTaskName.CREATE_PLAN, (success, fail) -> {
            PlanCreator planCreator = new PlanCreator(taskBuilder, targets);
            basisConsumer.accept(planCreator);
            try {

                success.success(null, Collections.singletonList(MessageValue.any(planCreator.getPlan())));
            } catch (UserException e) {
                e.printStackTrace();
                fail.failed(ErrorClass.BATCH_UPDATE, ErrorKind.UNKNOWN, singletonList(MessageValue.error(e)));
            }
        }).create();
    }


}
