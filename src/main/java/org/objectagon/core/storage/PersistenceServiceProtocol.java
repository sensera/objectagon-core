package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public interface PersistenceServiceProtocol extends Protocol<PersistenceServiceProtocol.Send, Protocol.Reply> {

    ProtocolName PERSISTENCE_SERVICE_PROTOCOL = new ProtocolNameImpl("PERSISTENCE_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        CREATE,
        UPDATE,
        REMOVE,
        GET,
        ALL,
    }

    interface Send extends Protocol.Send {
        Task create(Data data);
        Task update(Data data);
        Task remove(Identity identity, Version version);
        Task get(Identity identity, Version version);
        Task all(Identity identity);
    }
}
