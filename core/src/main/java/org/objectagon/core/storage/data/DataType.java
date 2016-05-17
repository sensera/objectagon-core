package org.objectagon.core.storage.data;

import org.objectagon.core.storage.Data;

import java.util.Objects;

/**
 * Created by christian on 2016-04-05.
 */
//@Value(staticConstructor = "create")
public class DataType implements Data.Type {
    String name;

    public String getName() {
        return name;
    }

    private DataType(String name) {
        this.name = name;
    }

    public static DataType create(String name) {
        return new DataType(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataType)) return false;
        DataType dataType = (DataType) o;
        return Objects.equals(getName(), dataType.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "DataType{" +
                "name='" + name + '\'' +
                '}';
    }
}
