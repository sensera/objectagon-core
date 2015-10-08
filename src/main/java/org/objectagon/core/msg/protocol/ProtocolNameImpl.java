package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class ProtocolNameImpl implements Protocol.ProtocolName {
    private String name;

    public ProtocolNameImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtocolNameImpl fieldName = (ProtocolNameImpl) o;
        return Objects.equals(name, fieldName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
