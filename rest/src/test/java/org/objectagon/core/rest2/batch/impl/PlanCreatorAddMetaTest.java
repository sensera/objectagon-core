package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.meta.MetaNameImpl;
import org.objectagon.core.object.method.InvokeParamImpl;
import org.objectagon.core.object.method.MethodNameImpl;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-04-15.
 */
public class PlanCreatorAddMetaTest {

    private final static MetaNameImpl META_NAME = MetaNameImpl.create("MetaName");
    private final static MethodNameImpl METHOD_NAME = MethodNameImpl.create("MethodName");
    private final Method.ParamName PARAM_NAME = ParamNameImpl.create("ParamName");
    private final NamedField NAMED_FIELD = NamedField.text("NamedField");
    private final Message.Value DEFAULT_VALUE = MessageValue.text("defaultValue");

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
            @Override public Optional<Meta.MetaName> getName() {
                return Optional.empty();
            }
            @Override public Stream<MethodPart> getMethods() {
                return Stream.empty();
            }
        });

        assertThat(planCreator.getPlan().actions.size(), is(1));
        final Actions.EntityProtocolAction createMetaAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
    }

    @Test
    public void addMetaName() throws Exception {
        planCreator.addMeta(new BatchUpdate.MetaBasis() {
            @Override public Optional<Meta.MetaName> getName() {
                return Optional.of(META_NAME);
            }
            @Override public Stream<MethodPart> getMethods() {
                return Stream.empty();
            }
        });

        assertThat(planCreator.getPlan().actions.size(), is(1));

        final Actions.EntityProtocolAction createMetaAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);

        assertThat(createMetaAction.filterName(META_NAME), is(true));
    }

    @Test
    public void addMetaMethod() throws Exception {
        planCreator.addMeta(new BatchUpdate.MetaBasis() {
            @Override public Optional<Meta.MetaName> getName() {
                return Optional.empty();
            }
            @Override public Stream<MethodPart> getMethods() {
                return Stream.of(new MethodPart() {
                    @Override public Optional<Method.MethodName> getName() {return Optional.of(METHOD_NAME);}
                    @Override public Optional<String> getCode() {return Optional.of("code");}
                    @Override public Stream<Method.InvokeParam> getInvokeParams() {
                        return Stream.of(InvokeParamImpl.create(
                                PARAM_NAME,
                                NAMED_FIELD,
                                DEFAULT_VALUE));
                    }
                });
            }
        });

        assertThat(planCreator.getPlan().actions.size(), is(4));

        final Actions.EntityProtocolAction createMetaAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        final Actions.SimpleTargetProtocolAction setMethodName = (Actions.SimpleTargetProtocolAction) planCreator.getPlan().actions.get(1);
        final Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>> setMethodCode = (Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>>) planCreator.getPlan().actions.get(2);
        final Actions.DataPortalTargetProtocolAction<Actions.AddMethodParamData> addMethodParam = (Actions.DataPortalTargetProtocolAction<Actions.AddMethodParamData>) planCreator.getPlan().actions.get(3);

        assertThat(createMetaAction.filterName(META_NAME), is(false));
        assertThat(setMethodName.filterName(METHOD_NAME), is(true));
        assertThat(setMethodCode.getDataPortal().get().getValue(), is(equalTo("code")));
        assertThat(addMethodParam.getDataPortal().get().getParamName(), is(equalTo(PARAM_NAME)));
        assertThat(addMethodParam.getDataPortal().get().getField(), is(equalTo(NAMED_FIELD)));
        assertThat(addMethodParam.getDataPortal().get().getDefaultValue(), is(equalTo(DEFAULT_VALUE)));
    }

}