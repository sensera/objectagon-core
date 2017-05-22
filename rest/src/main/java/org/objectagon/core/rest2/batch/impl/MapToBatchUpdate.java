package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.UnknownValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.field.StandardFieldType;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.object.meta.MetaNameImpl;
import org.objectagon.core.object.method.MethodNameImpl;
import org.objectagon.core.object.relationclass.RelationNameImpl;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.utils.NameValue;
import org.objectagon.core.utils.NameValueMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-04-15.
 */
public class MapToBatchUpdate {

    public static final Name NAME = StandardName.name("name");
    public static final Name TYPE = StandardName.name("type");

    public static final Name META = StandardName.name("meta");
    public static final Name CLASS = StandardName.name("class");
    public static final Name INSTANCE = StandardName.name("instance");

    public static final Name METHODS = StandardName.name("methods");
    public static final Name FIELDS = StandardName.name("fields");
    public static final Name VALUES = StandardName.name("values");
    public static final Name RELATIONS = StandardName.name("relations");
    public static final Name INSTANCES = StandardName.name("instances");
    public static final Name TARGET_CLASS = StandardName.name("targetClass");
    public static final Name TARGET_INSTANCE = StandardName.name("targetInstance");
    public static final Name CODE = StandardName.name("code");
    public static final Name VALUE = StandardName.name("value");
    public static final Name ALIAS = StandardName.name("alias");
    public static final Name INSTANCE_CLASS_NAME = StandardName.name("instanceClassName");


    public static Consumer<BatchUpdate.AddBasis> transfer(Message.Values values) {
        return planCreator -> {
            System.out.println("MapToBatchUpdate.transfer >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            MessageValueFieldUtil.create(values).stream()
                    .flatMap(value -> MessageValueFieldUtil.create(value.asValues()).stream())
                    .forEach(value -> {
                        try {
                            parseValueMap(planCreator, value);
                        } catch (UserException e) {
                            planCreator.parseError(e);
                        }
                    });
            System.out.println("MapToBatchUpdate.transfer <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        };
    }

    private static void parseValueMap(BatchUpdate.AddBasis planCreator, Message.Value value) throws UserException {
        if (!StandardField.MAP.equals(value.getField()))
            throw new UserException(ErrorClass.REST_SERVICE, ErrorKind.UNEXPECTED, MessageValue.name(value.getField().getName()));
        final Map.Entry<Name, Message.Value> base = value.asMap().entrySet().iterator().next();
        NameValueMap<Name, Message.Value> basisMap = NameValueMap.map(base.getValue().asMap());
        final Optional<Name> name = Optional.ofNullable(base.getKey());
        final Name type = basisMap.getText(TYPE)
                .map(StandardName::name)
                .orElseThrow(() -> new UserException(ErrorClass.REST_SERVICE, ErrorKind.TYPE_MISSING, value));
        try {
            if (META.equals(type)) {
                System.out.println("add META "+name.get());
                planCreator.addMeta(new BatchUpdate.MetaBasis() {
                    @Override public Stream<MethodPart> getMethods() {
                        return basisMap.getStreamMap(METHODS, MapToBatchUpdate::createMethodPart);
                    }
                    @Override public Optional<Meta.MetaName> getName() {
                        return name.map(MetaNameImpl::create);
                    }
                });
            }
            if (CLASS.equals(type)) {
                System.out.println("add CLASS "+name.get());
                planCreator.addClass(new BatchUpdate.ClassBasis() {
                    @Override public Stream<FieldPart> getFields() {
                        return basisMap.getStreamMap(FIELDS, MapToBatchUpdate::createFieldPart);
                    }
                    @Override public Stream<RelationPart> getRelations() {
                        return basisMap.getStreamMap(RELATIONS, MapToBatchUpdate::createClassRelationPart);
                    }
                    @Override public Optional<InstanceClass.InstanceClassName> getName() {
                        return name.map(Object::toString).map(InstanceClassNameImpl::create);
                    }
                });
            }
            if (INSTANCE.equals(type)) {
                System.out.println("add INSTANCE "+name.get());
                planCreator.addInstance(new BatchUpdate.InstanceBasis() {
                    @Override
                    public Optional<InstanceClass.InstanceClassName> getClassName() {
                        return basisMap.getName(CLASS, InstanceClassNameImpl::create);
                    }
                    @Override
                    public Optional<Name> getAlias() {
                        return basisMap.getName(ALIAS, StandardName::name);
                    }
                    @Override
                    public Stream<ValuePart> getValues() {
                        return basisMap.getStreamMap(VALUES, MapToBatchUpdate::createValuePart);
                    }
                    @Override public Stream<RelationPart> getRelations() {
                        return basisMap.getStreamMap(RELATIONS, MapToBatchUpdate::createRelationPart);
                    }
                    @Override public Optional<Name> getName() {
                        return name;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            planCreator.parseError(new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.PARSE_ERROR,
                    value,
                    name.map(MessageValue::name).orElse(UnknownValue.name()),
                    MessageValue.name(type),
                    MessageValue.exception(e)
            ));
        }
    }

    private static BatchUpdate.MetaBasis.MethodPart createMethodPart(NameValueMap<Name,Message.Value> methodValueMap) {
        final Optional<NameValue<Name, Message.Value>> root = methodValueMap.root();
        final Optional<NameValueMap<Name, Message.Value>> methodContents = root.flatMap(r -> methodValueMap.getMap(r.getKey()));
        final BatchUpdate.MetaBasis.MethodPart methodPart = new BatchUpdate.MetaBasis.MethodPart() {
            @Override public Optional<Method.MethodName> getName() {
                return root
                        .map(NameValue::getKey)
                        .map(Object::toString)
                        .map(MethodNameImpl::create);
            }
            @Override public Optional<String> getCode() {
                return methodContents
                        .map(nameValueNameValueMap -> nameValueNameValueMap.getText(CODE))
                        .orElse(Optional.empty());
            }
            @Override public Stream<Method.InvokeParam> getInvokeParams() {
                return Stream.empty();
            }
        };
        System.out.println("  create method "+methodPart.getName().map(Object::toString).orElse(""));
        methodPart.getCode().ifPresent(s -> System.out.println("  with code '"+s+"'"));
        return methodPart;
    }

    private static BatchUpdate.ClassBasis.FieldPart createFieldPart(NameValueMap<Name,Message.Value> fieldValueMap) {
        final Optional<NameValue<Name, Message.Value>> root = fieldValueMap.root();
        final Optional<NameValueMap<Name, Message.Value>> fieldContents = root.flatMap(r -> fieldValueMap.getMap(r.getKey()));
        final BatchUpdate.ClassBasis.FieldPart fieldPart = new BatchUpdate.ClassBasis.FieldPart() {
            @Override
            public Optional<Field.FieldName> getName() {
                return root
                        .map(NameValue::getKey)
                        .map(Object::toString)
                        .map(FieldNameImpl::create);
            }

            @Override
            public Optional<Field.FieldType> getType() {
                return fieldContents
                        .flatMap(nameValueNameValueMap -> nameValueNameValueMap.getText(TYPE))
                        .map(StandardFieldType::valueOf);
            }
        };
        System.out.println("  create field "+fieldPart.getName().map(Object::toString).orElse("")+" "+fieldPart.getType().map(Object::toString).orElse(""));
        return fieldPart;
    }

    private static BatchUpdate.ClassBasis.RelationPart createClassRelationPart(NameValueMap<Name,Message.Value> relationValueMap) {
        final Optional<NameValue<Name, Message.Value>> root = relationValueMap.root();
        final Optional<NameValueMap<Name, Message.Value>> relationContents = root.flatMap(r -> relationValueMap.getMap(r.getKey()));
        final BatchUpdate.ClassBasis.RelationPart relationPart = new BatchUpdate.ClassBasis.RelationPart() {
            @Override
            public Optional<RelationClass.RelationName> getName() {
                return root
                        .map(NameValue::getKey)
                        .map(Object::toString)
                        .map(RelationNameImpl::create);
            }

            @Override
            public Optional<InstanceClassNameImpl> getTargetInstanceClassIdentity() {
                return relationContents
                        .flatMap(nameValueNameValueMap -> nameValueNameValueMap.getText(TARGET_CLASS))
                        .map(InstanceClassNameImpl::create);
            }

            @Override
            public Optional<RelationClass.RelationType> getRelationType() {
                return relationValueMap.getText(TYPE).map(RelationClass.RelationType::valueOf);
            }
        };
        System.out.println("  create relation class "+relationPart.getName().map(Object::toString).orElse("")+" "+relationPart.getRelationType().map(Object::toString).orElse(""));
        relationPart.getTargetInstanceClassIdentity().ifPresent(instanceClassIdentity -> System.out.println("    to instance class '"+instanceClassIdentity+"'"));
        return relationPart;
    }

    private static BatchUpdate.InstanceBasis.ValuePart createValuePart(NameValueMap<Name,Message.Value> valueMap) {
        final Optional<NameValue<Name, Message.Value>> root = valueMap.root();
        final Optional<NameValueMap<Name, Message.Value>> valueContents = root.flatMap(r -> valueMap.getMap(r.getKey()));
        final BatchUpdate.InstanceBasis.ValuePart valuePart = new BatchUpdate.InstanceBasis.ValuePart() {
            @Override
            public Optional<Field.FieldName> getName() {
                return root
                        .map(NameValue::getKey)
                        .map(Object::toString)
                        .map(FieldNameImpl::create);
            }

            @Override
            public Optional<Message.Value> getValue() {
                return valueContents.flatMap(nameValueNameValueMap -> nameValueNameValueMap.get(VALUE));
            }
        };
        System.out.println("  create value "+valuePart.getName().map(Object::toString).orElse("")+" "+valuePart.getValue().map(Object::toString).orElse(""));
        return valuePart;
    }

    private static BatchUpdate.InstanceBasis.RelationPart createRelationPart(NameValueMap<Name,Message.Value> relationValueMap) {
        final Optional<NameValue<Name, Message.Value>> root = relationValueMap.root();
        //final Optional<NameValueMap<Name, Message.Value>> relationContents = root.flatMap(r -> relationValueMap.getMap(r.getKey()));
        final BatchUpdate.InstanceBasis.RelationPart relationPart = new BatchUpdate.InstanceBasis.RelationPart() {
            @Override
            public Optional<RelationClass.RelationName> getName() {
                return root
                        .map(NameValue::getKey)
                        .map(Object::toString)
                        .map(RelationNameImpl::create);
            }

            @Override
            public Stream<Name> getInstances() {
                return relationValueMap.getStream(RELATIONS).map(Message.Value::asName).map(v -> (Name) v);
            }
        };
        System.out.println("  create relation "+relationPart.getName().map(Object::toString).orElse("")+" "+relationPart.getInstances().map(Object::toString).collect(Collectors.joining(", ")));
        return relationPart;
    }

}
