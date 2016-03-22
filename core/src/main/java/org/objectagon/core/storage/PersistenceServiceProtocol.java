package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public interface PersistenceServiceProtocol extends Protocol<PersistenceServiceProtocol.Send, Protocol.Reply> {

    ProtocolName PERSISTENCE_SERVICE_PROTOCOL = new ProtocolNameImpl("PERSISTENCE_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        PUSH_DATA,
        PUSH_DATA_VERSION,
        PUSH_DATA_AND_VERSION,
        GET_DATA,
        GET_DATA_VERSION,
        ALL,
    }

    interface Send extends Protocol.Send {
        Task pushData(Data data);
        Task pushDataVersion(DataVersion dataVersion);
        Task pushDataAndVersion(Data data, DataVersion dataVersion);
        Task getData(Identity identity, Version version);
        Task getDataVersion(Identity identity, Version version);
        Task getLatestDataVersion(Identity identity);
        Task all(Identity identity);
    }

}
