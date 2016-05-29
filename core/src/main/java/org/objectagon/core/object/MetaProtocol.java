package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-29.
 */
public interface MetaProtocol extends Protocol<MetaProtocol.Send, Protocol.Reply> {

    ProtocolName META_PROTOCOL = new ProtocolNameImpl("META_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        CREATE_METHOD,
    }

    interface Send extends Protocol.Send {
        Task createMethod();
    }

}
