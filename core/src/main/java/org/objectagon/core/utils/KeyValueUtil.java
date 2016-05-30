package org.objectagon.core.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-05-30.
 */
public class KeyValueUtil<Key,Value> {
    public static <Key,Value> KeyValueUtil<Key,Value> create(List<KeyValue<Key, Value>> keyValueList) { return new KeyValueUtil<>(keyValueList); }

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
}
