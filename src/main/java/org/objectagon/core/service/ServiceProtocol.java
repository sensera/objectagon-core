package org.objectagon.core.service;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-08.
 */
public interface ServiceProtocol extends Protocol<ServiceProtocol.Session> {

    ClientSession createClientSession(Address address);

    ProtocolName SERVICE_PROTOCOL = new ProtocolNameImpl("SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        START_SERVICE,
        STOP_SERVICE,
        FAILED,
    }

    enum FieldName implements Message.Field {
        ERROR_DESCRIPTION("ERROR_DESCRIPTION", Message.FieldType.Text),
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
        void startService();
        void stopService();
    }

    enum ErrorKind {
        ServiceIsStoppning, ServiceIsStarting;
    }

}
