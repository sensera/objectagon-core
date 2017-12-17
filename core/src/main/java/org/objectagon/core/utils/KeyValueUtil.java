package org.objectagon.core.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-05-30.
 */
public class KeyValueUtil<Key,Value> {
    public static <Key,Value> KeyValueUtil<Key,Value> create(List<KeyValue<Key, Value>> keyValueList) { return new KeyValueUtil<>(keyValueList); }
    public static <Key,Value> Map<Key,Value> toMap(List<KeyValue<Key, Value>> keyValueList) { return new KeyValueUtil<>(keyValueList).asMap(); }

    List<KeyValue<Key,Value>> keyValueList;

    public KeyValueUtil(List<KeyValue<Key, Value>> keyValueList) {
        this.keyValueList = keyValueList;
    }

    public Optional<Value> get(Key key) {
        return keyValueList.stream()
                .filter(findByKey(key))
                .map(KeyValue::getValue)
                .findFirst();
    }

    public List<Value> getValues() {
        return keyValueList.stream().map(KeyValue::getValue).collect(Collectors.toList());
    }

    public List<Key> getKeys() {
        return keyValueList.stream().map(KeyValue::getKey).collect(Collectors.toList());
    }

    public List<Message.Value> createValues(Message.Field keyValueField, Function<Key, Message.Value> keyValueFunction, Function<Value, Message.Value> valueValueFunction) {
        return keyValueList.stream()
                .map(keyValueKeyValue -> MessageValue.values(
                        keyValueField,
                        keyValueFunction.apply(keyValueKeyValue.getKey()),
                        valueValueFunction.apply(keyValueKeyValue.getValue())))
                .collect(Collectors.toList());
    }

    private Predicate<KeyValue<Key, Value>> findByKey(Key key) {
        return keyValueKeyValue -> keyValueKeyValue.getKey().equals(key);
    }

    public static <Key,Value> List<KeyValue<Key,Value>> single(Key key, Value value) {
        return Collections.singletonList(new KeyValue<Key, Value>() {
            @Override
            public Key getKey() {
                return key;
            }

            @Override
            public Value getValue() {
                return value;
            }
        });
    }

    public static <Key,Value> KeyValue<Key,Value> createKeyValue(Key key, Value value) {
        return new KeyValue<Key, Value>() {
            @Override public Key getKey() {
                return key;
            }
            @Override public Value getValue() {
                return value;
            }
        };
    }

    public static <OriginalKey, NewKey, OrgValue, NewValue> List<KeyValue<NewKey,NewValue>> transformList(
            List<KeyValue<OriginalKey,OrgValue>> originalList,
            Function<OriginalKey, NewKey> transformKey,
            Function<OrgValue, NewValue> transformValue) {
        return originalList.stream()
                .map(paramNameValueKeyValue -> createKeyValue(transformKey.apply(paramNameValueKeyValue.getKey()),
                                                              transformValue.apply(paramNameValueKeyValue.getValue())))
                .collect(Collectors.toList());
    }

    public static <OriginalKey, NewKey, Value> List<KeyValue<NewKey,Value>> transformKey(List<KeyValue<OriginalKey,Value>> originalList, Function<OriginalKey, NewKey> transformKey) {
        return originalList.stream()
                .map(paramNameValueKeyValue -> createKeyValue(transformKey.apply(paramNameValueKeyValue.getKey()),
                        paramNameValueKeyValue.getValue()))
                .collect(Collectors.toList());
    }

    public static <Key, OrgValue, NewValue> List<KeyValue<Key,NewValue>> transformValues(List<KeyValue<Key,OrgValue>> originalList, Function<OrgValue, NewValue> transformValue) {
        return originalList.stream()
                .map(paramNameValueKeyValue -> createKeyValue(paramNameValueKeyValue.getKey(), transformValue.apply(paramNameValueKeyValue.getValue())))
                .collect(Collectors.toList());
    }

    public Map<Key,Value> asMap() {
        return keyValueList.stream()
                .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
    }
}
