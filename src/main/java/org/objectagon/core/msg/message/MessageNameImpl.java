package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class MessageNameImpl implements Message.MessageName {
    private String name;
    private String protocolName;

    public MessageNameImpl(String name, String protocolName) {
        this.name = name;
        this.protocolName = protocolName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageNameImpl that = (MessageNameImpl) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(protocolName, that.protocolName);
    }

    @Override
    public boolean same(Message.MessageName messageName) {
        return equals(messageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocolName);
    }
}
