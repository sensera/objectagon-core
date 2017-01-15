package org.objectagon.core.object.relationclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassProtocolImpl extends AbstractProtocol<RelationClassProtocol.Send, RelationClassProtocol.Reply> implements RelationClassProtocol {

    public static void registerAt(Server server) {
        server.registerFactory(RELATION_CLASS_PROTOCOL, new SingletonFactory<>(RelationClassProtocolImpl::new));
    }

    public RelationClassProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, RELATION_CLASS_PROTOCOL);
        createSend = RelationClassProtocolSend::new;
    }

    private class RelationClassProtocolSend extends AbstractProtocolSend implements RelationClassProtocol.Send {
        public RelationClassProtocolSend(Protocol.CreateSendParam sendParam) {
            super(sendParam);
        }


        @Override
        public Task createRelation(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo) {
            return task(MessageName.CREATE_RELATION,
                    send -> send.send(MessageName.CREATE_RELATION,
                            MessageValue.address(RelationClass.INSTANCE_CLASS_FROM, instanceIdentityFrom),
                            MessageValue.address(RelationClass.INSTANCE_CLASS_TO, instanceIdentityTo)));
        }

        @Override
        public Task destroyRelation(Instance.InstanceIdentity instanceIdentityFrom, Relation.RelationIdentity relationIdentity) {
            return task(MessageName.DESTROY_RELATION, send -> send.send(MessageName.DESTROY_RELATION,
                    MessageValue.address(RelationClass.INSTANCE_CLASS_FROM, instanceIdentityFrom),
                    MessageValue.address(Relation.RELATION_IDENTITY, relationIdentity)));

        }
    }
}
