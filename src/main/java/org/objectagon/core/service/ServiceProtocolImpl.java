package org.objectagon.core.service;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceProtocolImpl extends AbstractProtocol<ServiceProtocol.Session> implements ServiceProtocol {

    public ServiceProtocolImpl(Composer composer, Transporter transporter) {
        super(SERVICE_PROTOCOL, composer, transporter);
    }

    public ServiceProtocol.Session createSession(Address target) {
        return new ServiceProtocolSessionImpl(composer, target);
    }

    public ServiceProtocol.Session createSession(Composer composer, Address target) {
        return new ServiceProtocolSessionImpl(composer, target);
    }

    public ClientSession createClientSession(Address target) {
        return new ClientServiceProtocolSessionImpl(composer, target);
    }

    protected class ServiceProtocolSessionImpl extends AbstractProtocolSession implements ServiceProtocol.Session {

        public ServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void replyWithError(ErrorKind errorKind) {
            transporter.transport(composer.create(new StandardProtocol.ErrorMessage("",errorKind.name())));
        }
    }

    protected class ClientServiceProtocolSessionImpl extends AbstractProtocolSession implements ClientSession {

        public ClientServiceProtocolSessionImpl(Composer composer, Address target) {
            super(composer, target);
        }

        public void startService() {
            transporter.transport(composer.create(MessageName.START_SERVICE));
        }

        public void stopService() {
            transporter.transport(composer.create(MessageName.START_SERVICE));
        }

    }
}
