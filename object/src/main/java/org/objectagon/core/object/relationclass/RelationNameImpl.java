package org.objectagon.core.object.relationclass;

import org.objectagon.core.object.RelationClass;

import java.util.Objects;

/**
 * Created by christian on 2016-04-04.
 */
public class RelationNameImpl implements RelationClass.RelationName {
    private final String name;

    public String getName() {
        return name;
    }

    private RelationNameImpl(String name) {
        this.name = name;
    }

    public static RelationNameImpl create(String name) {
        return new RelationNameImpl(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationNameImpl)) return false;
        RelationNameImpl that = (RelationNameImpl) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
