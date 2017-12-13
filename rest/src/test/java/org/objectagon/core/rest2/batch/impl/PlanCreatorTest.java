package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-04-15.
 */
public class PlanCreatorTest {

    private PlanCreator planCreator;
    private TaskBuilder taskBuilder;
    private BatchUpdate.Targets targets;

    @Before
    public void setup() {
        taskBuilder = mock(TaskBuilder.class);
        targets = mock(BatchUpdate.Targets.class);
        planCreator = new PlanCreator(taskBuilder, targets);
    }

    @Test public void test() throws Exception {
        final PlanImpl plan = planCreator.getPlan();
        assertThat(plan.actions.size(), is(0));
    }
}