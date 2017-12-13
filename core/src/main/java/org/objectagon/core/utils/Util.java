package org.objectagon.core.utils;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.message.UnknownValue;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2015-11-29.
 */
public class Util {
    public static <E> Iterable<E> concat(E... values) {
        if (values==null || values.length==0)
            return Collections.EMPTY_LIST;
        return Arrays.asList(values);
    }

    public static <E> Optional<E> select(Iterable<E> values, Predicate<E> select) {
        return iterableToStream(values)
                .filter(select)
                .findAny();
    }

    public static void notImplemented()  {
        throw new RuntimeException("Not implemented!");
    }

    public static Message.Value getValueByField(Iterable<Message.Value> params, Message.Field field) {
        for (Message.Value value : params)
            if (value.getField().equals(field))
                return value;
        return UnknownValue.create(field);
    }

    public static boolean emptyOrNull(String value) {
        return value == null || value.equals("");
    }

    public static <T> Stream<T> iterableToStream(Iterable<T> iter) {
        return StreamSupport.stream(iter.spliterator(), false);
    }

    public static <T> List<T> iterableToList(Iterable<T> iter) {
        List<T> copy = new ArrayList<T>();
        iter.forEach(copy::add);
        return copy;
    }

    public static <T> T[] iterableToArray(Iterable<T> iter, Supplier<T[]> createArray) {
        List<T> copy = new ArrayList<T>();
        iter.forEach(copy::add);
        return copy.toArray(createArray.get());
    }

    public static <T> List<T> updateIfChange(List<T> original, List<Consumer<List<T>>> changes) {
        if (changes.isEmpty())
            return original;
        List<T> newFields = new ArrayList<>(original);
        changes.stream().forEach(listConsumer -> listConsumer.accept(newFields));
        return newFields;
    }

    public static ValuePrinter createValuePrinter() { return new ValuePrinter();}

    public static String printValuesToString(Message.Value[] params) {
        return printValuesToString(Arrays.asList(params));
    }

    public static String printValuesToString(Iterable<Message.Value> params) {
        boolean paramsIsEmpty = params == null || !params.iterator().hasNext();
        if (paramsIsEmpty)
            return "";
        return "[" + createValuePrinter().toString(params) + "]";
    }

    public static class ValuePrinter implements Message.Writer {
        StringBuffer sb = new StringBuffer();

        void append(String name, String value) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }


        @Override
        public void write(Message.Field field, String text) {
            append(field.getName().toString(), text);
        }

        @Override
        public void write(Message.Field field, Long number) {
            append(field.getName().toString(), ""+number);
        }

        @Override
        public void write(Message.Field field, MessageValueMessage message) {
            append(field.getName().toString(), ""+message.getMessageName()+"["+toString(message.getValues())+"]");
        }

        @Override
        public void write(Message.Field field, Message.MessageName messageName) {
            append(field.getName().toString(), ""+messageName);
        }

        @Override
        public void write(Message.Field field, Address address) {
            append(field.getName().toString(), ""+address);
        }

        @Override
        public void write(Message.Field field, Name name) {
            append(field.getName().toString(), ""+name);
        }

        @Override
        public void write(Message.Field field, Message.Values values) {
            append(field.getName().toString(), "["+toString(values.values())+"]");
        }

        @Override
        public void write(Message.Field field, Map<? extends Name, ? extends Message.Value> map) {
            String st = map.entrySet().stream()
                    .map(entry -> {
                        ValuePrinter valuePrinter = new ValuePrinter();
                        entry.getValue().writeTo(valuePrinter);
                        return entry.getKey().toString()+" {"+valuePrinter+"}";
                    })
                    .collect(Collectors.joining(", "));
            append(field.getName().toString(), "["+ st +"]");
        }

        @Override public <V> void writeAny(Message.Field field, V value) {
            if (value==null)
                append(field.getName().toString(), "null");
            else
                append(field.getName().toString(), ""+value.toString());
        }

        @Override
        public String toString() {
            return sb.toString();
        }

        public String toString(Iterable<Message.Value> params) {
            if (params==null)
                return "";
            ValuePrinter valuePrinter = new ValuePrinter();
            params.forEach(value -> value.writeTo(valuePrinter) );
            return valuePrinter.toString();
        }
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
