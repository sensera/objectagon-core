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
        GET_DATA,
        GET_LATEST_DATA,
        ALL_BY_ID,
        ALL_BY_TYPE,
    }

    interface Send extends Protocol.Send {
        Task pushData(Data... data);
        Task getData(Data.Type dataType, Identity identity, Version version);
        Task getLatestData(Data.Type dataType, Identity identity);
        Task all(Identity identity);
        Task all(Data.Type dataType);
    }

}
