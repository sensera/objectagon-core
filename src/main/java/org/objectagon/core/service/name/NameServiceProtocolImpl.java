package org.objectagon.core.service.name;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceProtocolImpl extends AbstractProtocol<NameServiceProtocol.Session> implements NameServiceProtocol {

    public NameServiceProtocolImpl(Composer composer, Transporter transporter) {
        super(NAME_SERVICE_PROTOCOL, composer, transporter);
    }

    public ClientSession createClientSession(Address target) {return new ClientNameServiceProtocolSessionImpl(composer, target);}
    public NameServiceProtocol.Session createSession(Address target) {return new NameServiceProtocolSessionImpl(composer, target);}
    public NameServiceProtocol.Session createSession(Composer composer, Address target) {return new NameServiceProtocolSessionImpl(composer, target);}

    protected class NameServiceProtocolSessionImpl extends AbstractProtocolSession implements NameServiceProtocol.Session {

        public NameServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void replyWithError(ErrorKind errorKind) {
            transporter.transport(composer.create(new StandardProtocol.ErrorMessage("",errorKind.name())));
        }

    }

    protected class ClientNameServiceProtocolSessionImpl extends AbstractProtocolSession implements NameServiceProtocol.ClientSession {

        public ClientNameServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void registerName(Address address, String name) {

        }

        public void unregisterName(Address address, String name) {

        }

        public void lookupAddressByName(String name) {

        }
    }

}
