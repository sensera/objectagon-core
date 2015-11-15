package org.objectagon.core.service.event;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;

/**
 * Created by christian on 2015-10-13.
 */
public interface EventServiceProtocol extends Protocol<EventServiceProtocol.Session> {

    ProtocolName EVENT_SERVICE_PROTOCOL = new ProtocolNameImpl("EVENT_SERVICE_PROTOCOL");

    NamedAddress EVENT_SERVICE_ADDRESS = new NamedAddress(EVENT_SERVICE_PROTOCOL);

    ClientSession createClientSession(Address target);

    enum MessageName implements Message.MessageName {
        START_LISTEN_TO,
        STOP_LISTEN_TO,
        BROADCAST,
    }

    enum FieldName implements Message.Field {
        ADDRESS("ADDRESS", Message.FieldType.Address),
        NAME("NAME", Message.FieldType.Name),
        MESSAGE("MESSAGE", Message.FieldType.Message),

        ERROR_DESCRIPTION("ERROR_SEVERITY", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }

    }

    interface Session extends Protocol.Session {
        void replyWithError(ErrorKind errorKind);

        void broadcast(Message message);

    }

    interface ClientSession extends Protocol.Session {
        void startListenTo(Address address, Name name);
        void stopListenTo(Address address, Name name);
        void broadcast(Name name, Message message);
    }

    enum ErrorKind {
    }
}
