package org.objectagon.core.utils;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by christian on 2016-04-03.
 */
//@Data(staticConstructor = "finder")
public class FindNamedConfiguration {
    public static FindNamedConfiguration finder(Receiver.Configurations[] configurations) {
        return new FindNamedConfiguration(configurations);
    }

    private final Receiver.Configurations[] configurations;

    public Receiver.Configurations[] getConfigurations() {
        return configurations;
    }

    private FindNamedConfiguration(Receiver.Configurations[] configurations) {
        this.configurations = configurations;
    }

    public <C extends Receiver.NamedConfiguration> C getConfigurationByName(Name name) {
        for (Receiver.Configurations configurations : this.configurations) {
            Optional<C> configurationByName = configurations.getConfigurationByName(name);
            if (configurationByName.isPresent())
                return configurationByName.get();
        }
        throw new SevereError(ErrorClass.RECEIVER, ErrorKind.MISSING_CONFIGURATION, MessageValue.name(name));
    }

    public <C extends Receiver.NamedConfiguration> Optional<C> getConfigurationByNameOptional(Name name) {
        for (Receiver.Configurations configurations : this.configurations) {
            Optional<C> configurationByName = configurations.getConfigurationByName(name);
            if (configurationByName.isPresent())
                return configurationByName;
        }
        return Optional.empty();
    }

    public <A extends Address> A createConfiguredAddress(CreateAddress<A> createAddress) {
        Receiver.AddressConfigurationParameters addressConfigurationParameters = getConfigurationByName(Receiver.ADDRESS_CONFIGURATIONS);
        return createAddress.create(addressConfigurationParameters.getServerId(), addressConfigurationParameters.getTimeStamp(), addressConfigurationParameters.getId());
    }

    @FunctionalInterface
    public interface CreateAddress<A extends Address> {
        A create(Server.ServerId serverId, long timestamp, long addressId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FindNamedConfiguration)) return false;
        FindNamedConfiguration that = (FindNamedConfiguration) o;
        return Arrays.equals(getConfigurations(), that.getConfigurations());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getConfigurations());
    }

    @Override
    public String toString() {
        return "FindNamedConfiguration{" +
                "configurations=" + Arrays.toString(configurations) +
                '}';
    }
}
