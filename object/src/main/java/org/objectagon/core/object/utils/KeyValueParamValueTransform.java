package org.objectagon.core.object.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;
import org.objectagon.core.utils.Util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-05-30.
 */
public class KeyValueParamValueTransform<P extends Name, V> {

    private final Message.Field FIELD_MAPPINGS;
    private final Message.Field DEFAULT_MAPPINGS;
    private final Message.Field KEY_VALUES;
    private final Message.Field PARAM_NAME;
    private final Message.Field VALUE_NAME;
    private final Function<P,Message.Value> paramTransformToMessageValue;
    private final Function<V,Message.Value> valueTransformToMessageValue;
    private final Function<Message.Value, P> messageValueTransformToParam;
    private final Function<Message.Value, V> messageValueTransformToValue;

    public KeyValueParamValueTransform(Message.Field FIELD_MAPPINGS, Message.Field DEFAULT_MAPPINGS, Message.Field KEY_VALUES, Message.Field PARAM_NAME, Message.Field VALUE_NAME, Function<P, Message.Value> paramTransformToMessageValue, Function<V, Message.Value> valueTransformToMessageValue, Function<Message.Value, P> messageValueTransformToParam, Function<Message.Value, V> messageValueTransformToValue) {
        this.FIELD_MAPPINGS = FIELD_MAPPINGS;
        this.DEFAULT_MAPPINGS = DEFAULT_MAPPINGS;
        this.KEY_VALUES = KEY_VALUES;
        this.PARAM_NAME = PARAM_NAME;
        this.VALUE_NAME = VALUE_NAME;
        this.paramTransformToMessageValue = paramTransformToMessageValue;
        this.valueTransformToMessageValue = valueTransformToMessageValue;
        this.messageValueTransformToParam = messageValueTransformToParam;
        this.messageValueTransformToValue = messageValueTransformToValue;
    }

    public Message.Transform<List<KeyValue<P, V>>> createFieldMappingsTransformer() {
        return new Message.Transform<List<KeyValue<P, V>>>() {


            @Override
            public Message.Value transform(List<KeyValue<P, V>> fieldMappings) {

                return MessageValue.values(FIELD_MAPPINGS,
                        KeyValueUtil.create(fieldMappings).createValues(
                                KEY_VALUES,
                                paramTransformToMessageValue,
                                valueTransformToMessageValue)
                );
            }

            @Override
            public List<KeyValue<P, V>> transform(Message.Value valueToTransform) {
                return Util.iterableToStream(valueToTransform.asValues().values())
                        .map(value -> {
                                final MessageValueFieldUtil lookup = MessageValueFieldUtil.create(value.asValues());
                                return new KeyIdentityImpl(
                                        messageValueTransformToParam.apply(lookup.getValueByField(PARAM_NAME)),
                                        messageValueTransformToValue.apply(lookup.getValueByField(VALUE_NAME)));
                            })
                        .collect(Collectors.toList());
            }
        };
    }

    public Message.Transform<List<KeyValue<P, Message.Value>>> createValuesTransformer() {
        return new Message.Transform<List<KeyValue<P, Message.Value>>>() {
            @Override
            public Message.Value transform(List<KeyValue<P, Message.Value>> defaultValues) {
                return MessageValue.values(DEFAULT_MAPPINGS,
                        KeyValueUtil.create(defaultValues).createValues(
                                KEY_VALUES,
                                (paramName) -> MessageValue.name(PARAM_NAME, paramName),
                                (messageValue) -> messageValue)
                );
            }

            @Override
            public List<KeyValue<P, Message.Value>> transform(Message.Value valueToTransform) {
                return Util.iterableToStream(valueToTransform.asValues().values())
                        .map(value -> {
                            final Iterator<Message.Value> iterator = value.asValues().values().iterator();
                            Message.Value value1 = iterator.next();
                            if (value1.getField().equals(PARAM_NAME))
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

    private class KeyIdentityImpl implements KeyValue<P, V> {
        private P key;
        private V field;

        public KeyIdentityImpl(P key, V field) {
            this.key = key;
            this.field = field;
        }

        @Override
        public P getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return field;
        }
    }

    private class KeyDefaultImpl implements KeyValue<P, Message.Value> {
        private P key;
        private Message.Value field;

        public KeyDefaultImpl(P key, Message.Value field) {
            this.key = key;
            this.field = field;
        }

        @Override
        public P getKey() {
            return key;
        }

        @Override
        public Message.Value getValue() {
            return field;
        }
    }

    public KeyValue<P, Message.Value> createKeyValue(P key, Message.Value field) {
        return new KeyDefaultImpl(key, field);
    }

}
