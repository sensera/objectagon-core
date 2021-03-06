package org.objectagon.core.task;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2016-04-10.
 */
public class StandardTaskBuilderAddress implements Address, TaskBuilder.TaskBuilderAddress {
    public static StandardTaskBuilderAddress create(Server.ServerId serverId, long timestamp, long addressId) { return new StandardTaskBuilderAddress(serverId, timestamp, addressId);}

    protected final Server.ServerId serverId;
    protected final long timestamp;
    protected final long addressId;

    protected StandardTaskBuilderAddress(Server.ServerId serverId, long timestamp, long addressId) {
        this.serverId = serverId;
        this.timestamp = timestamp;
        this.addressId = addressId;
    }

    @Override
    public Task.TaskAddress create(Task.TaskName taskName, long taskSequenceId) {
        return new StandardTaskAddress(serverId, timestamp, addressId,taskName, taskSequenceId);
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(Server.SERVER_ID).set(serverId);
        builderItem.create(TIMESTAMP).set(timestamp);
        builderItem.create(ADDRESS_ID).set(addressId);
    }
}
