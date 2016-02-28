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

    enum MessageName implements Message.MessageName, Task.TaskName {
        PUSH_DATA,
        PUSH_DATA_VERSION,
        GET_DATA,
        GET_DATA_VERSION,
        ALL,
    }

    interface Send extends Protocol.Send {
        Task pushData(Data data);
        Task pushDataVersion(DataVersion dataVersion);
        Task getData(Identity identity, Version version);
        Task getDataVersion(Identity identity, Version version);
        Task all(Identity identity);
    }
}
