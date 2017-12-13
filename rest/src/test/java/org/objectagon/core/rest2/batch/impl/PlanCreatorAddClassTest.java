package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.field.StandardFieldType;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.object.meta.MetaNameImpl;
import org.objectagon.core.object.method.MethodNameImpl;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.object.relationclass.RelationNameImpl;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-04-15.
 */
public class PlanCreatorAddClassTest {

    private final FieldNameImpl FIELD_NAME = FieldNameImpl.create("FieldName");
    private final RelationNameImpl RELATION_NAME = RelationNameImpl.create("RelationName");
    private final InstanceClassNameImpl INSTANCE_CLASS_NAME = InstanceClassNameImpl.create("InstanceClassName");
    private final static MethodNameImpl METHOD_NAME = MethodNameImpl.create("MethodName");
    private final static MetaNameImpl META_NAME = MetaNameImpl.create("MetaName");
    private final Method.ParamName PARAM_NAME = ParamNameImpl.create("ParamName");
    private final NamedField NAMED_FIELD = NamedField.text("NamedField");
    private final Message.Value DEFAULT_VALUE = MessageValue.text("defaultValue");

    private PlanCreator planCreator;
    private TaskBuilder taskBuilder;
    private BatchUpdate.Targets targets;

    @Before public void setup() {
        taskBuilder = mock(TaskBuilder.class);
        targets = mock(BatchUpdate.Targets.class);
        planCreator = new PlanCreator(taskBuilder, targets);
    }

    @Test public void addClass() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Optional<InstanceClass.InstanceClassName> getName() {
                return Optional.empty();
            }
            @Override public Stream<FieldPart> getFields() {
                return Stream.empty();
            }
            @Override public Stream<RelationPart> getRelations() {
                return Stream.empty();
            }
            @Override public Stream<MethodPart> getMethods()  {
                return Stream.empty();
            }
        });
        assertThat(planCreator.getPlan().actions.size(), is(1));
        final Actions.EntityProtocolAction createClassAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        assertThat(createClassAction.filterName(INSTANCE_CLASS_NAME), is(false));
    }

    @Test public void addClassWithName() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Optional<InstanceClass.InstanceClassName> getName() {return Optional.of(INSTANCE_CLASS_NAME);}
            @Override public Stream<FieldPart> getFields() {return Stream.empty();}
            @Override public Stream<RelationPart> getRelations() {return Stream.empty();}
            @Override public Stream<MethodPart> getMethods()  {
                return Stream.empty();
            }
        });
        assertThat(planCreator.getPlan().actions.size(), is(1));
        final Actions.EntityProtocolAction createClassAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        assertThat(createClassAction.filterName(INSTANCE_CLASS_NAME), is(true));
    }

    @Test public void addClassWithField() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Optional<InstanceClass.InstanceClassName> getName() {
                return Optional.empty();
            }
            @Override public Stream<FieldPart> getFields() {
                return Stream.of(new FieldPart() {
                    @Override public Optional<Field.FieldName> getName() {return Optional.of(FIELD_NAME);}
                    @Override public Optional<Field.FieldType> getType() {return Optional.of(StandardFieldType.TEXT);}
                    @Override public Optional<String> getDefaultValue() {return Optional.of("DefaultValue");}
                });
            }
            @Override public Stream<RelationPart> getRelations() {
                return Stream.empty();
            }
            @Override public Stream<MethodPart> getMethods()  {
                return Stream.empty();
            }
        });
        assertThat(planCreator.getPlan().actions.size(), is(5));
        final Actions.EntityProtocolAction createClassAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        assertThat(createClassAction.filterName(INSTANCE_CLASS_NAME), is(false));

        final Actions.SimpleTargetProtocolAction addClassFieldAction = (Actions.SimpleTargetProtocolAction) planCreator.getPlan().actions.get(1);
        final Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>> setFieldName = (Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>>) planCreator.getPlan().actions.get(2);
        assertThat(setFieldName.getDataPortal().get().getName(), is(equalTo(FIELD_NAME)));

        Actions.DataPortalTargetProtocolAction<Actions.SetValue<Field.FieldType>> setFieldType = (Actions.DataPortalTargetProtocolAction<Actions.SetValue<Field.FieldType>>) planCreator.getPlan().actions.get(3);
        assertThat(setFieldType.getDataPortal().get().getValue(), is(equalTo(StandardFieldType.TEXT)));

        Actions.DataPortalTargetProtocolAction<Actions.SetValue<Message.Value>> setFieldDefaultValue = (Actions.DataPortalTargetProtocolAction<Actions.SetValue<Message.Value>>) planCreator.getPlan().actions.get(4);
        assertThat(setFieldDefaultValue.getDataPortal().get().getValue(), is(equalTo("DefaultValue")));
    }

    @Test public void addClassWithRelation() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Optional<InstanceClass.InstanceClassName> getName() {return Optional.empty();}
            @Override public Stream<FieldPart> getFields() {return Stream.empty();}
            @Override public Stream<RelationPart> getRelations() {
                return Stream.of(new RelationPart() {
                    @Override public Optional<RelationClass.RelationName> getName() {return Optional.of(RELATION_NAME);}
                    @Override public Optional<InstanceClass.InstanceClassName> getTargetInstanceClassName() {return Optional.of(INSTANCE_CLASS_NAME);}
                    @Override public Optional<RelationClass.RelationType> getRelationType() {return Optional.of(RelationClass.RelationType.ASSOCIATION);}
                });
            }
            @Override public Stream<MethodPart> getMethods()  {
                return Stream.empty();
            }
        });
        assertThat(planCreator.getPlan().actions.size(), is(3));
        final Actions.EntityProtocolAction createRelationAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        assertThat(createRelationAction.filterName(INSTANCE_CLASS_NAME), is(false));

        final Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData> addClassRelationAction = (Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData>) planCreator.getPlan().actions.get(1);
        assertThat(addClassRelationAction.getDataPortal().get().getRelationType(),is(equalTo(RelationClass.RelationType.ASSOCIATION)));
        assertThat(addClassRelationAction.getDataPortal().get().getRelatedClass(),is(nullValue(InstanceClass.InstanceClassIdentity.class))); //TODO add test for instance lookup

        final Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>> setRelationName = (Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>>) planCreator.getPlan().actions.get(2);
        assertThat(setRelationName.getDataPortal().get().getName(), is(equalTo(RELATION_NAME)));
    }

    @Test public void addClassWithMethod() throws Exception {
        planCreator.addClass(new BatchUpdate.ClassBasis() {
            @Override public Optional<InstanceClass.InstanceClassName> getName() {return Optional.empty();}
            @Override public Stream<FieldPart> getFields() {return Stream.empty();}
            @Override public Stream<RelationPart> getRelations() {return Stream.empty();}
            @Override public Stream<MethodPart> getMethods()  {
                return Stream.of(new MethodPart() {
                    @Override public Optional<Method.MethodName> getName() {return Optional.of(METHOD_NAME);}
                    @Override public Optional<Meta.MetaName> getMetaName() {return Optional.of(META_NAME);}
                    @Override public Optional<Method.MethodName> getMetaMethodName() {return Optional.of(METHOD_NAME);}
                    @Override public Stream<MethodPartDetail> getMappedParams() {
                        return Stream.of(new MethodPartDetail() {
                            @Override public Method.ParamName getParamName() {return PARAM_NAME;}
                            @Override public Field.FieldName getFieldName() {return FIELD_NAME;}
                            @Override public Optional<Message.Value> getDefaultValue() {return Optional.of(DEFAULT_VALUE);}
                        });
                    }
                });
            }
        });
        assertThat(planCreator.getPlan().actions.size(), is(2));
        final Actions.EntityProtocolAction createRelationAction = (Actions.EntityProtocolAction) planCreator.getPlan().actions.get(0);
        assertThat(createRelationAction.filterName(INSTANCE_CLASS_NAME), is(false));

        final Actions.DataPortalTargetProtocolAction<Actions.AddClassMethodData> addClassMethodAction = (Actions.DataPortalTargetProtocolAction<Actions.AddClassMethodData>) planCreator.getPlan().actions.get(1);
        assertThat(addClassMethodAction.getDataPortal().get().getMethodIdentity(),is(nullValue(Method.MethodIdentity.class)));
        assertThat(addClassMethodAction.getDataPortal().get().getDefaultValues().count(),is(1L));
        assertThat(addClassMethodAction.getDataPortal().get().getFieldMappings().count(),is(0L));
    }
}