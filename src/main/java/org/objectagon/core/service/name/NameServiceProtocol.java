package org.objectagon.core.service.name;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public interface NameServiceProtocol extends Protocol<NameServiceProtocol.Session> {

    ProtocolName NAME_SERVICE_PROTOCOL = new ProtocolNameImpl("NAME_SERVICE_PROTOCOL");

    NamedAddress NAME_SERVICE_ADDRESS = new NamedAddress(NAME_SERVICE_PROTOCOL);

    ClientSession createClientSession(Address target);

    enum MessageName implements Message.MessageName {
        REGISTER_NAME,
        UNREGISTER_NAME,
        LOOKUP_ADDRESS_BY_NAME,
    }

    enum FieldName implements Message.Field {
        ADDRESS("ADDRESS", Message.FieldType.Address),
        NAME("NAME", Message.FieldType.Text),

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

    }

    interface ClientSession extends Protocol.Session {
        void registerName(Address address, String name);
        void unregisterName(Address address, String name);
        void lookupAddressByName(String name);
    }

    enum ErrorKind implements StandardProtocol.ErrorKind {
        NameAlreadyRegistered,
        NameNotFound;
    }

}
