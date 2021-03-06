package org.objectagon.core.msg.name;

import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2016-01-06.
 */
public class StandardName implements Name {
    String name;

    private StandardName(String name) {
        this.name = name;
    }

    public static StandardName name(String name) {
        return new StandardName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardName)) return false;
        StandardName that = (StandardName) o;
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
