package org.objectagon.core.utils;

import lombok.Data;
import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;

import java.util.Optional;

/**
 * Created by christian on 2016-04-03.
 */
@Data(staticConstructor = "finder")
public class FindNamedConfiguration {
    private final Receiver.Configurations[] configurations;

    public <C extends Receiver.NamedConfiguration> C getConfigurationByName(Name name) {
        for (Receiver.Configurations configurations : this.configurations) {
            Optional<C> configurationByName = configurations.getConfigurationByName(name);
            if (configurationByName.isPresent())
                return configurationByName.get();
        }
        throw new SevereError(ErrorClass.RECEIVER, ErrorKind.MISSING_CONFIGURATION, MessageValue.name(name));
    }

    public <A extends Address> A createConfiguredAddress(CreateAddress<A> createAddress) {
        Receiver.AddressConfigurationParameters addressConfigurationParameters = getConfigurationByName(Receiver.ADDRESS_CONFIGURATIONS);
        return createAddress.create(addressConfigurationParameters.getServerId(), addressConfigurationParameters.getTimeStamp(), addressConfigurationParameters.getId());
    }

    @FunctionalInterface
    public interface CreateAddress<A extends Address> {
        A create(Server.ServerId serverId, long timestamp, long addressId);
    }
}
