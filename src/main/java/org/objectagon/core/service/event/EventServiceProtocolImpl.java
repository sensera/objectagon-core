package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.Arrays;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceProtocolImpl extends AbstractProtocol<EventServiceProtocol.Session>  implements EventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(EVENT_SERVICE_PROTOCOL, EventServiceProtocolImpl::new);
    }

    public EventServiceProtocolImpl() {
        super(EventServiceProtocol.EVENT_SERVICE_PROTOCOL);
    }

    @Override
    public EventServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new EventServiceProtocolSessionImpl(nextSessionId(), composer, transporter, sessionOwner);
    }

    protected class EventServiceProtocolSessionImpl extends AbstractProtocolSession implements EventServiceProtocol.Session {

        public EventServiceProtocolSessionImpl(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }

        public void startListenTo(Address address, Name name) {
            transport(composer.create(
                    SimpleMessage.simple(MessageName.START_LISTEN_TO)
                            .setValue(StandardField.ADDRESS, address)
                            .setValue(StandardField.NAME, name)));
        }

        public void stopListenTo(Address address, Name name) {
            transport(composer.create(
                    SimpleMessage.simple(MessageName.STOP_LISTEN_TO)
                            .setValue(StandardField.ADDRESS, address)
                            .setValue(StandardField.NAME, name)));
        }

        @Override
        public void broadcast(Name name, Message.MessageName message, Message.Value... values) {
            SimpleMessage simpleMessage = SimpleMessage.simple(MessageName.BROADCAST)
                    .setValue(StandardField.NAME, name)
                    .setValue(StandardField.MESSAGE, message);
            Arrays.asList(values).forEach(simpleMessage::setValue);
            transport(composer.create(
                    simpleMessage));
        }
    }

}
