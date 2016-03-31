package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Entity;

/**
 * Created by christian on 2016-02-28.
 */
public interface EntityServiceActionInitializer extends Reactor.ActionInitializer {
    Address getPersistencyService();

    Entity.EntityConfig createEntityConfigForInitialization(DataVersion dataVersion, Long counter, MessageValueFieldUtil messageValueFieldUtil);
}
