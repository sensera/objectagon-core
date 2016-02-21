package org.objectagon.core.service.name;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-13.
 */
public interface NameServiceProtocol extends Protocol<NameServiceProtocol.Send, Protocol.Reply> {

    ProtocolName NAME_SERVICE_PROTOCOL = new ProtocolNameImpl("NAME_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        REGISTER_NAME,
        UNREGISTER_NAME,
        LOOKUP_ADDRESS_BY_NAME,
    }

    interface Send extends Protocol.Send {
        Task registerName(Address address, Name name);
        Task unregisterName(Name name);
        Task lookupAddressByName(Name name);
    }

    enum NameServiceErrorKind implements StandardProtocol.ErrorKind {
        NameAlreadyRegistered,
        NameNotFound;
    }

}
