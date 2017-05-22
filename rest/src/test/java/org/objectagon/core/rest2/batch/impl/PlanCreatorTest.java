package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.Meta;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.stream.Stream;

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

    @Test
    public void addMeta() throws Exception {
        planCreator.addMeta(new BatchUpdate.MetaBasis() {
            @Override public Stream<MethodPart> getMethods() {return Stream.empty();}
            @Override public Optional<Meta.MetaName> getName() {return Optional.empty();}
        });

        assertThat(planCreator.getPlan().actions.size(), is(1));
        //assertThat(planCreator.getPlan().actions.get(0), is(instanceOf(Actions.CreateMetaAction.class)));
        //final CreateMetaAction createMetaAction = (CreateMetaAction) planCreator.getPlan().actions.get(0);
    }

    @Test
    public void addClass() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Stream<FieldPart> getFields() {return Stream.empty();}
            @Override public Stream<RelationPart> getRelations() {return Stream.empty();}
            @Override public Optional<InstanceClass.InstanceClassName> getName() {return Optional.empty();}
        });
        assertThat(planCreator.getPlan().actions.size(), is(1));
        //assertThat(planCreator.getPlan().actions.get(0), is(instanceOf(Actions.CreateClassAction.class)));
        //final CreateClassAction createClassAction = (CreateClassAction) planCreator.getPlan().actions.get(0);
    }
}