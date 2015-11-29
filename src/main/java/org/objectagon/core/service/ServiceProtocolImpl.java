package org.objectagon.core.service;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceProtocolImpl extends AbstractProtocol<ServiceProtocol.Session> implements ServiceProtocol {

    public ServiceProtocolImpl() {
        super(SERVICE_PROTOCOL);
    }


    @Override
    public ServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new ServiceProtocolSessionImpl(nextSessionId(), composer, transporter, sessionOwner);
    }

    protected class ServiceProtocolSessionImpl extends AbstractProtocolSession implements ServiceProtocol.Session {

        public ServiceProtocolSessionImpl(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }

        public void replyWithError(ErrorKind errorKind) {
            transport(composer.create(new StandardProtocol.ErrorMessage("","",errorKind.name())));
        }

        public void startService() {
            send(MessageName.START_SERVICE);
        }

        public void stopService() {
            send(MessageName.START_SERVICE);
        }

    }

}
