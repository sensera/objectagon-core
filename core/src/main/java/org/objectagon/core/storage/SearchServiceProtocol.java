package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-04-09.
 */
public interface SearchServiceProtocol extends Protocol<SearchServiceProtocol.Send, Protocol.Reply> {

    ProtocolName SEARCH_SERVICE_PROTOCOL = new ProtocolNameImpl("SEARCH_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        NAME_SEARCH,
        NAME_CHANGED_EVENT
    }

    interface Send extends Protocol.Send {
        Task nameSearch(Name name);
    }


}
