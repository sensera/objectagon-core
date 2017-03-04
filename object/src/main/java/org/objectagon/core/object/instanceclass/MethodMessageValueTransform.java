package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.Method;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;
import org.objectagon.core.utils.Util;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-05-30.
 */
public class MethodMessageValueTransform {

    public Message.Transform<List<KeyValue<Method.ParamName, Field.FieldIdentity>>> createFieldMappingsTransformer() {
        return new Message.Transform<List<KeyValue<Method.ParamName, Field.FieldIdentity>>>() {
            @Override
            public Message.Value transform(List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings) {
                return MessageValue.values(InstanceClassProtocol.METHOD_FIELD_MAPPINGS,
                        KeyValueUtil.create(fieldMappings).createValues(
                                InstanceClassProtocol.KEY_VALUES,
                                (paramName) -> MessageValue.name(Method.PARAM_NAME, paramName),
                                (fieldIdentity) -> MessageValue.address(Field.FIELD_IDENTITY, fieldIdentity))
                );
            }

            @Override
            public List<KeyValue<Method.ParamName, Field.FieldIdentity>> transform(Message.Value valueToTransform) {
                return Util.iterableToStream(valueToTransform.asValues().values())
                        .map(value -> {
                                final MessageValueFieldUtil lookup = MessageValueFieldUtil.create(value.asValues());
                                return new KeyIdentityImpl(
                                        lookup.getValueByField(Method.PARAM_NAME).asName(),
                                        lookup.getValueByField(Field.FIELD_IDENTITY).asAddress());
                            })
                        .collect(Collectors.toList());
            }
        };
    }

    public Message.Transform<List<KeyValue<Method.ParamName, Message.Value>>> createValuesTransformer() {
        return new Message.Transform<List<KeyValue<Method.ParamName, Message.Value>>>() {
            @Override
            public Message.Value transform(List<KeyValue<Method.ParamName, Message.Value>> defaultValues) {
                return MessageValue.values(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS,
                        KeyValueUtil.create(defaultValues).createValues(
                                InstanceClassProtocol.KEY_VALUES,
                                (paramName) -> MessageValue.name(Method.PARAM_NAME, paramName),
                                (messageValue) -> messageValue)
                );
            }

            @Override
            public List<KeyValue<Method.ParamName, Message.Value>> transform(Message.Value valueToTransform) {
                return Util.iterableToStream(valueToTransform.asValues().values())
                        .map(value -> {
                            final Iterator<Message.Value> iterator = value.asValues().values().iterator();
                            Message.Value value1 = iterator.next();
                            if (value1.getField().equals(Method.PARAM_NAME))
                                return new KeyDefaultImpl(
                                        value1.asName(),
                                        iterator.next());
                            return new KeyDefaultImpl(
                                    iterator.next().asName(),
                                    value1);
                        })
                        .collect(Collectors.toList());
            }
        };
    }

    private class KeyIdentityImpl implements KeyValue<Method.ParamName, Field.FieldIdentity> {
        private Method.ParamName key;
        private Field.FieldIdentity field;

        public KeyIdentityImpl(Method.ParamName key, Field.FieldIdentity field) {
            this.key = key;
            this.field = field;
        }

        @Override
        public Method.ParamName getKey() {
            return key;
        }

        @Override
        public Field.FieldIdentity getValue() {
            return field;
        }
    }

    private class KeyDefaultImpl implements KeyValue<Method.ParamName, Message.Value> {
        private Method.ParamName key;
        private Message.Value field;

        public KeyDefaultImpl(Method.ParamName key, Message.Value field) {
            this.key = key;
            this.field = field;
        }

        @Override
        public Method.ParamName getKey() {
            return key;
        }

        @Override
        public Message.Value getValue() {
            return field;
        }
    }

    public KeyValue<Method.ParamName, Message.Value> createKeyValue(Method.ParamName key, Message.Value field) {
        return new KeyDefaultImpl(key, field);
    }

}
