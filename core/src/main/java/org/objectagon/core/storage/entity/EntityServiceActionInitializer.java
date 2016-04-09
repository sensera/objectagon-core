package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.receiver.Reactor;

import java.util.Optional;

/**
 * Created by christian on 2016-02-28.
 */
public interface EntityServiceActionInitializer extends Reactor.ActionInitializer {

    Optional<Receiver.NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil);

}
