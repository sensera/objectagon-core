package org.objectagon.core.storage.persitence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Send, Protocol.Reply> implements PersistenceServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(PERSISTENCE_SERVICE_PROTOCOL, PersistenceServiceProtocolImpl::new);
    }

    public PersistenceServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(PERSISTENCE_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public PersistenceServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new PersistenceServiceProtocolSend(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }


    private class PersistenceServiceProtocolSend extends AbstractProtocolSend implements PersistenceServiceProtocol.Send {
        public PersistenceServiceProtocolSend(CreateSendParam createSend) {
            super(createSend);
        }

        @Override
        public Task create(Data data) {

            return null;
        }

        @Override
        public Task update(Data data) {

            return null;
        }

        @Override
        public Task remove(Identity identity, Version version) {

            return null;
        }

        @Override
        public Task get(Identity identity, Version version) {

            return null;
        }

        @Override
        public Task all(Identity identity) {

            return null;
        }
    }
}
