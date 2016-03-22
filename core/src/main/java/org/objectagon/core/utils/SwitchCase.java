package org.objectagon.core.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by christian on 2016-03-17.
 */
public class SwitchCase<E> {
    public static <E> SwitchCase<E> create(E value) { return new SwitchCase<>(value);}

    E value;
    boolean switched = false;

    private SwitchCase(E value) {
        this.value = value;
    }

    public SwitchCase<E> caseDo(E equals, Consumer<E> action) {
        if (!switched && Objects.equals(equals,value)) {
            switched = true;
            action.accept(value);
        }
        return this;
    }

    public SwitchCase<E> caseDo(Predicate<E> check, Consumer<E> action) {
        if (!switched && check.test(value)) {
            switched = true;
            action.accept(value);
        }
        return this;
    }

    public void elseDo(Consumer<E> action) {
        if (!switched) {
            switched = true;
            action.accept(value);
        }
    }
}
