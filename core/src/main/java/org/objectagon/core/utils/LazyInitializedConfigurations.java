package org.objectagon.core.utils;

import lombok.NoArgsConstructor;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2016-04-03.
 */
@NoArgsConstructor(staticName = "create")
public class LazyInitializedConfigurations implements Receiver.Configurations {

    private Map<Name, Create> createsByName = new HashMap<>();

    public void add(Name name, Create create) {
        createsByName.put(name, create);
    }

    @Override
    public <C extends Receiver.NamedConfiguration> Optional<C> getConfigurationByName(Name name) {
        return Optional.ofNullable(createsByName.get(name)).map(create -> (C) create.create());
    }

    @FunctionalInterface
    public interface Create {
        Receiver.NamedConfiguration create();
    }
}
