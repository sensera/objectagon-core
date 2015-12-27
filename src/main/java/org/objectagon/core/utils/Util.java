package org.objectagon.core.utils;

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
}
