package org.objectagon.core.object.method;

import org.objectagon.core.object.Method;

import java.util.Objects;

/**
 * Created by christian on 2016-04-04.
 */
public class MethodNameImpl implements Method.MethodName {
    private final String name;

    private MethodNameImpl(String name) {
        this.name = name;
    }

    public static MethodNameImpl create(String name) {
        return new MethodNameImpl(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodNameImpl)) return false;
        MethodNameImpl that = (MethodNameImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MethodNameImpl{" +
                "name='" + name + '\'' +
                '}';
    }
}
