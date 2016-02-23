package org.objectagon.core.storage.persistence;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;

/**
 * Created by christian on 2016-02-23.
 */
public interface PersistenceServiceActionInitializer extends Reactor.ActionInitializer {
    void store(Identity identity, Version version, Message.Values values);

    Message.Values get(Identity identity, Version version);

    void remove(Identity identity, Version version);

    void all(Identity identity, AllVersions allVersions);

    @FunctionalInterface
    interface AllVersions {
        void valuesVersions(Version version, Message.Values values);
    }
}
