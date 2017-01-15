package org.objectagon.core.rest2;

import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2017-01-09.
 */
public class ParamName implements Name {
    private String name;

    public static ParamName create(String name) {
        return new ParamName(name);
    }

    private ParamName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamName that = (ParamName) o;
        return Objects.equals(name, that.name);
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
