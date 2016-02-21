package org.objectagon.core.service.event;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public interface BroadcastEventServiceProtocol extends Protocol<BroadcastEventServiceProtocol.Send, Protocol.Reply> {

    ProtocolName BROADCAST_EVENT_SERVICE_PROTOCOL = new ProtocolNameImpl("BROADCAST_EVENT_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        BROADCAST,
    }

    interface Send extends Protocol.Send {
        void broadcast(Message.MessageName message, Iterable<Message.Value> values);
    }
}
