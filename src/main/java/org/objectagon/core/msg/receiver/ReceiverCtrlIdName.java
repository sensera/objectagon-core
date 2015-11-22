package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Receiver;

import java.util.Objects;

/**
 * Created by christian on 2015-11-22.
 */
public class ReceiverCtrlIdName implements Receiver.CtrlId {

    private String name;

    public ReceiverCtrlIdName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiverCtrlIdName that = (ReceiverCtrlIdName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ReceiverCtrlIdName{" +
                "name='" + name + '\'' +
                '}';
    }
}
