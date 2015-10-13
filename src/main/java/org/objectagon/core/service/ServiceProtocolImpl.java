package org.objectagon.core.service;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceProtocolImpl implements ServiceProtocol {
    private Composer composer;
    private Transporter transporter;

    public ProtocolName getName() {
        return SERVICE_PROTOCOL;
    }

    public ServiceProtocolImpl(Composer composer, Transporter transporter) {
        this.composer = composer;
        this.transporter = transporter;
    }

    public Session createSession(Address target) {
        return new ServiceProtocolSessionImpl(composer, target);
    }

    public Session createSession(Composer composer, Address target) {
        return new ServiceProtocolSessionImpl(composer, target);
    }

    public ClientSession createClientSession(Address target) {
        return new ClientServiceProtocolSessionImpl(composer, target);
    }

    protected class ServiceProtocolSessionImpl implements Session {
        final Address target;
        final Composer composer;

        public ServiceProtocolSessionImpl(Composer composer, Address target) {
            this.composer = composer;
            this.target = target;
        }

        public void replyWithError(String description, StandardProtocol.ErrorKind errorKind) {

        }

        public void replyOk() {

        }

        public void failed(String description, ErrorKind errorKind) {

        }
    }

    protected class ClientServiceProtocolSessionImpl implements ClientSession {
        final Address target;
        final Composer composer;

        public ClientServiceProtocolSessionImpl(Composer composer, Address target) {
            this.composer = composer;
            this.target = target;
        }

        public void startService() {
            transporter.transport(composer.create(MessageName.START_SERVICE));
        }

        public void stopService() {
            transporter.transport(composer.create(MessageName.START_SERVICE));
        }

    }
}
