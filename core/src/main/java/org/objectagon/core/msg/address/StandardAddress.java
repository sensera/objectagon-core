package org.objectagon.core.msg.address;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-10-06.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardAddress implements Address {
    protected final Server.ServerId serverId;
    protected final long timestamp;
    protected final long addressId;

    public static StandardAddress standard(Server.ServerId serverId, long timestamp, long addressId) { return new StandardAddress(serverId, timestamp, addressId);}
    public static StandardAddress fromConfig(Receiver.AddressConfigurationParameters addressConfigurationParameters) {
        return new StandardAddress(addressConfigurationParameters.getServerId(), addressConfigurationParameters.getTimeStamp(), addressConfigurationParameters.getId());
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(Server.SERVER_ID).set(serverId);
        builderItem.create(TIMESTAMP).set(timestamp);
        builderItem.create(ADDRESS_ID).set(addressId);
    }
}
