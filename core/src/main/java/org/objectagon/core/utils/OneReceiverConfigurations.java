package org.objectagon.core.utils;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by christian on 2016-04-03.
 */
public class OneReceiverConfigurations implements Receiver.Configurations {
    final private Name name;
    final private Receiver.NamedConfiguration target;

    public Name getName() {
        return name;
    }

    public Receiver.NamedConfiguration getTarget() {
        return target;
    }

    private OneReceiverConfigurations(Name name, Receiver.NamedConfiguration target) {
        this.name = name;
        this.target = target;
    }

    public static OneReceiverConfigurations create(Name name, Receiver.NamedConfiguration target) {
        return new OneReceiverConfigurations(name, target);
    }

    @Override
    public <C extends Receiver.NamedConfiguration> Optional<C> getConfigurationByName(Name name) {
        if (this.name.equals(name))
            return Optional.of( (C) target);
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OneReceiverConfigurations)) return false;
        OneReceiverConfigurations that = (OneReceiverConfigurations) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getTarget(), that.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTarget());
    }

    @Override
    public String toString() {
        return "OneReceiverConfigurations{" +
                "name=" + name +
                ", target=" + target +
                '}';
    }
}
