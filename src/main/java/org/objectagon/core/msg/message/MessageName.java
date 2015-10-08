package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.BasicAddress;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class MessageName implements Message.Name {
    private String name;
    private String protocolName;

    public MessageName(String name, String protocolName) {
        this.name = name;
        this.protocolName = protocolName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageName that = (MessageName) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(protocolName, that.protocolName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocolName);
    }
}
