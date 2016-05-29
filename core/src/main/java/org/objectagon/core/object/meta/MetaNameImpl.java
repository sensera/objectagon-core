package org.objectagon.core.object.meta;

import org.objectagon.core.object.Meta;

import java.util.Objects;

/**
 * Created by christian on 2016-04-04.
 */
public class MetaNameImpl implements Meta.MetaName {
    private final String name;

    private MetaNameImpl(String name) {
        this.name = name;
    }

    public static MetaNameImpl create(String name) {
        return new MetaNameImpl(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaNameImpl)) return false;
        MetaNameImpl that = (MetaNameImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MetaNameImpl{" +
                "name='" + name + '\'' +
                '}';
    }
}
