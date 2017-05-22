package org.objectagon.core.object.instanceclass;

import org.objectagon.core.object.InstanceClass;

import java.util.Objects;

/**
 * Created by christian on 2016-04-04.
 */
public class InstanceClassNameImpl implements InstanceClass.InstanceClassName {
    private final String name;

    private InstanceClassNameImpl(String name) {
        this.name = name;
    }

    public static InstanceClassNameImpl create(String name) {
        return new InstanceClassNameImpl(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceClassNameImpl)) return false;
        InstanceClassNameImpl that = (InstanceClassNameImpl) o;
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
