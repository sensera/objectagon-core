package org.objectagon.core.utils;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by christian on 2015-11-29.
 */
public class Util {
    public static <E> Iterable<E> concat(E... values) {
        if (values==null || values.length==0)
            return Collections.EMPTY_LIST;
        return Arrays.asList(values);
    }

    public static <E> Optional<E> select(Iterable<E> values, Select<E> select) {
        for (E value : values)
            if (select.select(value))
                return Optional.of(value);
        return Optional.empty();
    }

    public static void notImplemented()  {
        throw new RuntimeException("Not implemented!");
    }

    @FunctionalInterface
    public interface Select<E> {
        boolean select(E value);
    }

    public static ValuePrinter createValuePrinter() { return new ValuePrinter();}

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
        public void write(Message.Field field, Message message) {
            append(field.getName().toString(), ""+message.getName());
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
        public String toString() {
            return sb.toString();
        }

        public String toString(Iterable<Message.Value> params) {
            if (params==null)
                return "";
            params.forEach(value -> value.writeTo(this) );
            return toString();
        }
    }
}
