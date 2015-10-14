package org.objectagon.core.service.event;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceProtocolImpl extends AbstractProtocol<EventServiceProtocol.Session>  implements EventServiceProtocol {

    public EventServiceProtocolImpl(Composer composer, Transporter transporter) {
        super(EventServiceProtocol.EVENT_SERVICE_PROTOCOL, composer, transporter);
    }

    public ClientSession createClientSession(Address target) {
        return new ClientEventServiceProtocolSessionImpl(composer, target);
    }

    public EventServiceProtocol.Session createSession(Address target) {
        return new EventServiceProtocolSessionImpl(composer, target);
    }

    public EventServiceProtocol.Session createSession(Composer composer, Address target) {
        return new EventServiceProtocolSessionImpl(composer, target);
    }

    protected class EventServiceProtocolSessionImpl extends AbstractProtocolSession implements EventServiceProtocol.Session {

        public EventServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void replyWithError(ErrorKind errorKind) {
            transporter.transport(composer.create(new StandardProtocol.ErrorMessage("",errorKind.name())));
        }

        public void broadcast(Message message) {
            transporter.transport(composer.create(message));
        }
    }

    protected class ClientEventServiceProtocolSessionImpl extends AbstractProtocolSession implements EventServiceProtocol.ClientSession {

        public ClientEventServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void startListenTo(Address address, Name name) {
            transporter.transport(composer.create(new SimpleMessage(MessageName.START_LISTEN_TO).setValue(FieldName.ADDRESS, address).setValue(FieldName.NAME, name)));
        }

        public void stopListenTo(Address address, Name name) {
            transporter.transport(composer.create(new SimpleMessage(MessageName.STOP_LISTEN_TO).setValue(FieldName.ADDRESS, address).setValue(FieldName.NAME, name)));
        }

        public void broadcast(Name name, Message message) {
            transporter.transport(composer.create(new SimpleMessage(MessageName.BROADCAST).setValue(FieldName.MESSAGE, message).setValue(FieldName.NAME, name)));
        }
    }

}
