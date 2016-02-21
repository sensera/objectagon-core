package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-17.
 */
public class EntityServiceProtocolImpl extends AbstractProtocol<EntityServiceProtocol.Send, EntityServiceProtocol.Reply> implements EntityServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(ENTITY_SERVICE_PROTOCOL, EntityServiceProtocolImpl::new);
    }

    public EntityServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(ENTITY_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public EntityServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new EntityServiceProtocolSend(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }

    private class EntityServiceProtocolSend extends AbstractProtocolSend implements EntityServiceProtocol.Send {
        public EntityServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task create(Version version) {
            return task(EntityServiceProtocol.TaskName.Create, send -> send.send(MessageName.CREATE, version.asValue()));
        }

        @Override
        public Task get(Version version, Identity identity) {
            return task(EntityServiceProtocol.TaskName.Get, send -> send.send(MessageName.GET, version.asValue(), VolatileAddressValue.address(identity)));
        }
    }
}
