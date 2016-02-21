package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2016-01-11.
 */
public class EntityName implements Name {
    private String name;

    public static EntityName create(String name) {
        return new EntityName(name);
    }

    private EntityName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityName that = (EntityName) o;
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
