package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-17.
 */
public class EntityServiceProtocolImpl extends AbstractProtocol<EntityServiceProtocol.Send, EntityServiceProtocol.Reply> implements EntityServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(ENTITY_SERVICE_PROTOCOL, new SingletonFactory<>(EntityServiceProtocolImpl::new));
    }

    public EntityServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, ENTITY_SERVICE_PROTOCOL);
        createSend = EntityServiceProtocolSend::new;
    }

    private class EntityServiceProtocolSend extends AbstractProtocolSend implements EntityServiceProtocol.Send {
        public EntityServiceProtocolSend(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task create(Message.Value... initialValues) {
            return task(MessageName.CREATE_ENTITY, send -> send.send(MessageName.CREATE_ENTITY, MessageValue.values(initialValues)));
        }

        @Override
        public Task get(Identity identity) {
            return task(MessageName.GET_ENTITY, send -> send.send(MessageName.GET_ENTITY, MessageValue.address(identity)));
        }

        @Override
        public Task delete(Identity identity) {
            return task(MessageName.DELETE_ENTITY, send -> send.send(MessageName.DELETE_ENTITY, MessageValue.address(identity)));
        }
    }
}
