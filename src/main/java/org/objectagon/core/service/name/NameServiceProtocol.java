package org.objectagon.core.service.name;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.ReceiverCtrlIdName;

/**
 * Created by christian on 2015-10-13.
 */
public interface NameServiceProtocol extends Protocol<NameServiceProtocol.Session> {

    ProtocolName NAME_SERVICE_PROTOCOL = new ProtocolNameImpl("NAME_SERVICE_PROTOCOL");

    NamedAddress NAME_SERVICE_ADDRESS = new NamedAddress(NAME_SERVICE_PROTOCOL);

    ReceiverCtrlIdName NAME_SERVICE_CTRL_NAME = new ReceiverCtrlIdName("NameService");

    enum MessageName implements Message.MessageName {
        REGISTER_NAME,
        UNREGISTER_NAME,
        LOOKUP_ADDRESS_BY_NAME,
    }

    interface Session extends Protocol.Session {
        void registerName(Address address, Name name);
        void unregisterName(Name name);
        void lookupAddressByName(Name name);
    }

    enum ErrorKind implements StandardProtocol.ErrorKind {
        NameAlreadyRegistered,
        NameNotFound;
    }

}
