package org.objectagon.core.utils;

import lombok.Data;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;

import java.util.Optional;

/**
 * Created by christian on 2016-04-03.
 */
@Data(staticConstructor = "create")
public class OneReceiverConfigurations implements Receiver.Configurations {
    final private Name name;
    final private Receiver.NamedConfiguration target;

    @Override
    public <C extends Receiver.NamedConfiguration> Optional<C> getConfigurationByName(Name name) {
        if (this.name.equals(name))
            return Optional.of( (C) target);
        return Optional.empty();
    }
}
