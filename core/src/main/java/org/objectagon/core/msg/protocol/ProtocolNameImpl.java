package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Protocol;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class ProtocolNameImpl implements Protocol.ProtocolName {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public ProtocolNameImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtocolNameImpl)) return false;
        ProtocolNameImpl that = (ProtocolNameImpl) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "ProtocolNameImpl{" +
                "name='" + name + '\'' +
                '}';
    }
}
