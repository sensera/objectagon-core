package org.objectagon.core.service.name;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNameValue;
import org.objectagon.core.msg.message.VolatileTextValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileNameValue.name;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceProtocolImpl extends AbstractProtocol<NameServiceProtocol.Session> implements NameServiceProtocol {

    public NameServiceProtocolImpl() {
        super(NAME_SERVICE_PROTOCOL);
    }

    @Override
    public NameServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new NameServiceProtocolSessionImpl(nextSessionId(), composer, transporter, sessionOwner);
    }

    protected class NameServiceProtocolSessionImpl extends AbstractProtocolSession implements NameServiceProtocol.Session {

        public NameServiceProtocolSessionImpl(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }

        @Override
        public void registerName(Address address, Name name) {
            transport(composer.create(
                    MessageName.REGISTER_NAME,
                    address(StandardField.ADDRESS, address),
                    name(StandardField.NAME, name)));
        }

        @Override
        public void unregisterName(Name name) {
            transport(composer.create(
                    MessageName.UNREGISTER_NAME,
                    name(StandardField.NAME, name)));
        }

        @Override
        public void lookupAddressByName(Name name) {
            transport(composer.create(
                    MessageName.LOOKUP_ADDRESS_BY_NAME,
                    name(StandardField.NAME, name)));
        }
    }
}
