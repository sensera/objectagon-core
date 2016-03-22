package org.objectagon.core.service.event;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;

/**
 * Created by christian on 2015-10-13.
 */
public interface EventServiceProtocol extends Protocol<EventServiceProtocol.Send, Protocol.Reply> {

    ProtocolName EVENT_SERVICE_PROTOCOL = new ProtocolNameImpl("EVENT_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        START_LISTEN_TO,
        STOP_LISTEN_TO,
        BROADCAST,
    }

    interface Send extends Protocol.Send {
        void startListenTo(Address address, Name name);
        void stopListenTo(Address address, Name name);
        void broadcast(Name name, Message.MessageName message, Message.Value... values);
    }

}
