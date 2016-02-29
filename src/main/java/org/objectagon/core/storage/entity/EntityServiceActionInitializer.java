package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.receiver.Reactor;

/**
 * Created by christian on 2016-02-28.
 */
public interface EntityServiceActionInitializer extends Reactor.ActionInitializer {
    Address getPersistencyService();
}
