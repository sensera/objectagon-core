package org.objectagon.core.object.method;

import org.objectagon.core.object.Method;

import java.util.Objects;

/**
 * Created by christian on 2016-05-29.
 */
public class ParamNameImpl implements Method.ParamName {

    public static Method.ParamName create(String name) { return new ParamNameImpl(name);}

    String name;

    ParamNameImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParamNameImpl)) return false;
        ParamNameImpl paramName = (ParamNameImpl) o;
        return Objects.equals(name, paramName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
