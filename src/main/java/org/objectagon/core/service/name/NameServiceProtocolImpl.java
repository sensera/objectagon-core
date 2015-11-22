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

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceProtocolImpl extends AbstractProtocol<NameServiceProtocol.Session> implements NameServiceProtocol {

    public NameServiceProtocolImpl(Composer composer, Transporter transporter) {
        super(NAME_SERVICE_PROTOCOL, composer, transporter);
    }

    public NameServiceProtocol.Session createSession(Address target) {return new NameServiceProtocolSessionImpl(composer, target);}
    public NameServiceProtocol.Session createSession(Composer composer, Address target) {return new NameServiceProtocolSessionImpl(composer, target);}

    protected class NameServiceProtocolSessionImpl extends AbstractProtocolSession implements NameServiceProtocol.Session {

        public NameServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        @Override
        public void replyWithError(org.objectagon.core.exception.ErrorKind errorKind) {
            transporter.transport(composer.create(new StandardProtocol.ErrorMessage(StandardProtocol.ErrorSeverity.Unknown, ErrorClass.UNKNOWN, errorKind)));
        }

        @Override
        public void registerName(Address address, Name name) {
            transporter.transport(composer.create(
                    MessageName.REGISTER_NAME,
                    new VolatileAddressValue(StandardField.ADDRESS, address),
                    new VolatileNameValue(StandardField.NAME, name)));
        }

        @Override
        public void unregisterName(Name name) {
            transporter.transport(composer.create(
                    MessageName.UNREGISTER_NAME,
                    new VolatileNameValue(StandardField.NAME, name)));
        }

        @Override
        public void lookupAddressByName(Name name) {
            transporter.transport(composer.create(
                    MessageName.LOOKUP_ADDRESS_BY_NAME,
                    new VolatileNameValue(StandardField.NAME, name)));
        }
    }
}
