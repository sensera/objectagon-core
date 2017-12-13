package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.field.StandardFieldType;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.object.meta.MetaNameImpl;
import org.objectagon.core.object.method.InvokeParamImpl;
import org.objectagon.core.object.method.MethodNameImpl;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.object.relationclass.RelationNameImpl;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.model.Model;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-04-15.
 */
public class MapToBatchUpdate {

    public static Consumer<BatchUpdate.AddBasis> transfer(Model model) {
        return planCreator -> {
            try {
                model.getMetas().stream().forEach(meta -> planCreator.addMeta(new LocalMetaBasis(meta)));
                model.getClasses().stream().forEach(instanceClass -> planCreator.addClass(new LocalClassBasis(instanceClass)));
                model.getInstances().stream().forEach(instance -> planCreator.addInstance(new LocalInstanceBasis(instance)));
            } catch (Exception e) {
                e.printStackTrace();
                planCreator.parseError(new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.PARSE_ERROR,
                        MessageValue.any(model),
                        MessageValue.exception(e)
                ));
            }
        };
    }

    private static class LocalMethodPart implements BatchUpdate.MetaBasis.MethodPart {
        private final Model.Method method;

        public LocalMethodPart(Model.Method method) {
            this.method = method;
        }

        @Override public Optional<Method.MethodName> getName() {
            return Optional.ofNullable(method.getName())
                    .map(MethodNameImpl::create);
        }

        @Override public Optional<String> getCode() {
            return Optional.ofNullable(method.getCode());
        }

        @Override public Stream<Method.InvokeParam> getInvokeParams() {
            return method.getParams().stream().map(methodParam -> {
                final NamedField paramField = NamedField.text("paramField");
                final NamedField defaultValueField = NamedField.text("defaultValue");
                Method.InvokeParam invokeParam = InvokeParamImpl.create(
                        ParamNameImpl.create(methodParam.getName()),
                        paramField,
                        MessageValue.text(defaultValueField, methodParam.getDefaultValue())
                );
                return invokeParam;
            });
        }
    }

    private static class LocalMetaBasis implements BatchUpdate.MetaBasis {
        private final Model.Meta meta;

        public LocalMetaBasis(Model.Meta meta) {
            this.meta = meta;
        }

        @Override public Stream<MethodPart> getMethods() {
            return meta.getMethods().stream().map(LocalMethodPart::new);
        }

        @Override public Optional<Meta.MetaName> getName() {
            return Optional.ofNullable(meta.getName())
                    .map(MetaNameImpl::create);
        }
    }

    private static class LocalFieldPart implements BatchUpdate.ClassBasis.FieldPart {
        private final Model.Field field;

        public LocalFieldPart(Model.Field field) {
            this.field = field;
        }

        @Override public Optional<Field.FieldName> getName() {
            return Optional.ofNullable(field.getName())
                    .map(FieldNameImpl::create);
        }

        @Override public Optional<Field.FieldType> getType() {
            return Optional.ofNullable(field.getType())
                    .map(String::toUpperCase)
                    .map(StandardFieldType::valueOf);
        }

        @Override public Optional<String> getDefaultValue() {
            return Optional.ofNullable(field.getDefaultValue());
        }
    }

    private static class LocalClassRelationPart implements BatchUpdate.ClassBasis.RelationPart {
        private final Model.RelationClass relationClass;

        public LocalClassRelationPart(Model.RelationClass relationClass) {
            this.relationClass = relationClass;
        }

        @Override public Optional<RelationClass.RelationName> getName() {
            return Optional.ofNullable(relationClass.getName())
                    .map(RelationNameImpl::create);
        }

        @Override public Optional<InstanceClass.InstanceClassName> getTargetInstanceClassName() {
            return Optional.ofNullable(relationClass.getTarget())
                    .map(InstanceClassNameImpl::create);
        }

        @Override public Optional<RelationClass.RelationType> getRelationType() {
            return Optional.ofNullable(relationClass.getType())
                    .map(String::toUpperCase)
                    .map(RelationClass.RelationType::valueOf);
        }
    }

    private static class LocalClassMethodPart implements BatchUpdate.ClassBasis.MethodPart {
        private final Model.ClassMethod classMethod;

        public LocalClassMethodPart(Model.ClassMethod classMethod) {
            this.classMethod = classMethod;
        }

        @Override public Optional<Method.MethodName> getName() {
            return Optional.ofNullable(classMethod.getName())
                    .map(MethodNameImpl::create);
        }

        @Override public Optional<Meta.MetaName> getMetaName() {
            return Optional.ofNullable(classMethod.getMeta())
                    .map(MetaNameImpl::create);
        }

        @Override public Optional<Method.MethodName> getMetaMethodName() {
            return Optional.ofNullable(classMethod.getMethod())
                    .map(MethodNameImpl::create);
        }

        @Override public Stream<BatchUpdate.ClassBasis.MethodPartDetail> getMappedParams() {
            return classMethod.getParams().stream()
                    .map(classMethodParam -> new BatchUpdate.ClassBasis.MethodPartDetail() {
                        @Override public Method.ParamName getParamName() {return ParamNameImpl.create(classMethodParam.getParam());}
                        @Override public Field.FieldName getFieldName() {return FieldNameImpl.create(classMethodParam.getField());}
                        @Override public Optional<Message.Value> getDefaultValue() {
                            return Optional.of(classMethodParam.getDefaultValue()).map(MessageValue::any);
                        }
                    });
        }

    }

    private static class LocalClassBasis implements BatchUpdate.ClassBasis {
        private final Model.InstanceClass instanceClass;

        public LocalClassBasis(Model.InstanceClass instanceClass) {
            this.instanceClass = instanceClass;
        }

        @Override public Stream<FieldPart> getFields() {
            return instanceClass.getFields().stream()
                .map(LocalFieldPart::new);
        }

        @Override public Stream<RelationPart> getRelations() {
            return instanceClass.getRelationClasses().stream()
                    .map(LocalClassRelationPart::new);
        }

        @Override public Optional<InstanceClass.InstanceClassName> getName() {
            return Optional.ofNullable(instanceClass.getName())
                    .map(InstanceClassNameImpl::create);
        }

        @Override public Stream<MethodPart> getMethods() {
            return instanceClass.getMethods().stream()
                    .map(LocalClassMethodPart::new);
        }
    }

    private static class LocalValuePart implements BatchUpdate.InstanceBasis.ValuePart {
        private final Model.Value value;

        public LocalValuePart(Model.Value value) {
            this.value = value;
        }

        @Override
        public Optional<Field.FieldName> getName() {
            return Optional.ofNullable(value.getField())
                    .map(FieldNameImpl::create);
        }

        @Override
        public Optional<Message.Value> getValue() {
            return Optional.ofNullable(value.getValue()).map(MessageValue::text);
        }
    }

    private static class LocalRelationPart implements BatchUpdate.InstanceBasis.RelationPart {
        private final Model.Relation relation;

        public LocalRelationPart(Model.Relation relation) {
            this.relation = relation;
        }

        @Override public Optional<RelationClass.RelationName> getName() {
            return Optional.ofNullable(relation.getRelationClass())
                    .map(RelationNameImpl::create);
        }

        @Override public Optional<Name> getInstanceName() {
            return Optional.ofNullable(relation.getTargetInstance())
                    .map(StandardName::name);
        }
    }

    private static class LocalInstanceBasis implements BatchUpdate.InstanceBasis {
        private final Model.Instance instance;

        public LocalInstanceBasis(Model.Instance instance) {
            this.instance = instance;
        }

        @Override public Optional<InstanceClass.InstanceClassName> getClassName() {
            return Optional.ofNullable(instance.getClassName())
                    .map(InstanceClassNameImpl::create);
        }

        @Override public Optional<Name> getAlias() {
            return Optional.ofNullable(instance.getAlias())
                    .map(StandardName::name);
        }

        @Override public Stream<ValuePart> getValues() {
            return instance.getValues().stream()
                    .map(LocalValuePart::new);
        }

        @Override public Stream<RelationPart> getRelations() {
            return instance.getRelations().stream()
                    .map(LocalRelationPart::new);
        }

        @Override public Optional<Name> getName() {
            return Optional.ofNullable(instance.getName())
                    .map(StandardName::name);
        }
    }
}
