package org.objectagon.core.service.name;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileNameValue.name;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceProtocolImpl extends AbstractProtocol<NameServiceProtocol.Session> implements NameServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(NAME_SERVICE_PROTOCOL, serverId -> new NameServiceProtocolImpl(serverId));
    }

    public NameServiceProtocolImpl(Server.ServerId serverId) {
        super(NAME_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public NameServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new NameServiceProtocolSessionImpl(nextSessionId(), sessionOwner);
    }

    protected class NameServiceProtocolSessionImpl extends AbstractProtocolSession implements NameServiceProtocol.Session {

        public NameServiceProtocolSessionImpl(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
        }

        @Override
        public Task registerName(Address address, Name name) {
            return task(
                    NameServiceProtocol.TaskName.REGISTER_NAME,
                    send -> send.send(MessageName.REGISTER_NAME,
                            address(StandardField.ADDRESS, address),
                            name(StandardField.NAME, name)));
        }

        @Override
        public Task unregisterName(Name name) {
            return task(
                    NameServiceProtocol.TaskName.UNREGISTER_NAME,
                    send -> send.send(MessageName.REGISTER_NAME,
                            name(StandardField.NAME, name)));
        }

        @Override
        public Task lookupAddressByName(Name name) {
            return task(
                    NameServiceProtocol.TaskName.LOOKUP_ADDRESS,
                    send -> send.send(MessageName.LOOKUP_ADDRESS_BY_NAME, name(StandardField.NAME, name)));
        }

    }

}
