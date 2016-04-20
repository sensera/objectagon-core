package org.objectagon.core.task;

import lombok.Getter;
import org.objectagon.core.Server;

/**
 * Created by christian on 2016-04-10.
 */
public class StandardTaskAddress extends StandardTaskBuilderAddress implements Task.TaskAddress {
    @Getter private final Task.TaskName name;
    private final long taskSequenceId;

    public StandardTaskAddress(Server.ServerId serverId, long timestamp, long addressId, Task.TaskName name, long taskSequenceId) {
        super(serverId, timestamp, addressId);
        this.name = name;
        this.taskSequenceId = taskSequenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StandardTaskAddress) {
            StandardTaskAddress standardTaskAddress = (StandardTaskAddress) o;
            return super.equals(o)
                    && name == standardTaskAddress.name
                    && taskSequenceId == standardTaskAddress.taskSequenceId;
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "StandardTaskAddress{" +
                "name=" + name +
                ", taskSequenceId=" + taskSequenceId +
                '}';
    }
}
