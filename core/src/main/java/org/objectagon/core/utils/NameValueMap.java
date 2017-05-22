package org.objectagon.core.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.storage.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2017-04-23.
 */
public class NameValueMap<N extends Name, V extends Message.Value> {

    public static <N extends Name, V extends Message.Value> NameValueMap<N,V> map(Map<N,V> map) {
        return new NameValueMap<>(map);
    }

    public static <N extends Name, V extends Message.Value> NameValueMap<N,V> list(List<KeyValue<N,V>> list) {
        return new NameValueMap<>(list.stream().collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue)));
    }

    public static NameValueMap empty() {
        return new NameValueMap<>(new HashMap<>());
    }

    final Map<N,V> map;

    public NameValueMap(Map<N,V> map) {
        this.map = map;
    }

    public Optional<V> get(N name) {
        return Optional.ofNullable(map.get(name));
    }

    public Optional<Name> getName(N name) {
        return Optional.ofNullable(map.get(name)).map(Message.Value::asName);
    }

    public Optional<String> getText(N name) {
        return Optional.ofNullable(map.get(name)).map(Message.Value::asText);
    }

    public <Named extends Name> Optional<Named> getName(N name, Function<String, Named> createNamed) {
        return Optional.ofNullable(map.get(name))
                .map(Message.Value::asText)
                .map(Object::toString)
                .map(createNamed);
    }

    public Stream<Message.Value> getStream(N name) {
        return get(name)
                .map(v -> StreamSupport.stream(v.asValues().values().spliterator(), false))
                .orElse(Stream.empty());
    }

    public <L,MName extends Name, MValue extends Message.Value> Stream<L> getStreamMap(N name, Function<NameValueMap<MName,MValue>,L> trans) {
        return getStream(name)
                .map(value -> trans.apply(NameValueMap.map(value.asMap())));
    }

    public <I extends Identity> Optional<I> getIdentity(N name) {
        return get(name).map(Message.Value::asAddress);
    }

    public NameValueMap<N,V> copy() {
        return new NameValueMap<>(new HashMap<>(map));
    }

    public NameValueMap<N,V> extend(Consumer<ExtendNameValueMap<N,V>> extendNameValueMapConsumer) {
        final HashMap<N, V> extendedMap = new HashMap<>(this.map);
        extendNameValueMapConsumer.accept(extendedMap::put);
        return new NameValueMap<>(extendedMap);
    }

    public NameValueMap<N,V> extendNameValue(Consumer<Consumer<NameValue<N,V>>> extendNameValueMapConsumer) {
        final HashMap<N, V> extendedMap = new HashMap<>(this.map);
        extendNameValueMapConsumer.accept(nameValue -> extendedMap.put(nameValue.getKey(), nameValue.getValue()));
        return new NameValueMap<>(extendedMap);
    }

    public Optional<NameValue<N,V>> root() {
        if (map.isEmpty())
            return Optional.empty();
        final Map.Entry<N, V> root = map.entrySet().iterator().next();
        return Optional.of(new NameValue<>(root.getKey(), root.getValue()));
    }

    public Optional<NameValueMap<N,V>> getMap(N name) {
        return get(name).map(v -> NameValueMap.map(v.asMap()));
    }

    public NameValueMap<N,V> extend(Function<Iterable<V>, Optional<NameValue<N,V>>> extender) {
        return extender.apply(this.map.values()).map(nvNameValue -> {
            final HashMap<N, V> extendedMap = new HashMap<>(this.map);
            extendedMap.put(nvNameValue.getKey(), nvNameValue.getValue());
            return new NameValueMap<>(extendedMap);
        }).orElse(this);
    }

    @FunctionalInterface
    public interface ExtendNameValueMap<N extends Name, V extends Message.Value> {
        void addNamedValue(N name, V value);
    }

}
