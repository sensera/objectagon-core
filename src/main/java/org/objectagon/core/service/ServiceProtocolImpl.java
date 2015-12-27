package org.objectagon.core.service;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceProtocolImpl extends AbstractProtocol<ServiceProtocol.Session> implements ServiceProtocol {

    public ServiceProtocolImpl(Server.ServerId serverId) {
        super(SERVICE_PROTOCOL, serverId);
    }


    @Override
    public ServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new ServiceProtocolSessionImpl(nextSessionId(), sessionOwner);
    }

    protected class ServiceProtocolSessionImpl extends AbstractProtocolSession implements ServiceProtocol.Session {

        public ServiceProtocolSessionImpl(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
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
