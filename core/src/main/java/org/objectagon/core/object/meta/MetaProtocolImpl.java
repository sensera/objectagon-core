package org.objectagon.core.object.meta;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaProtocolImpl extends AbstractProtocol<MetaProtocol.Send, Protocol.Reply> implements MetaProtocol {
    public static void registerAt(Server server) {
        server.registerFactory(META_PROTOCOL, new SingletonFactory<>(MetaProtocolImpl::new));
    }

    public MetaProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, META_PROTOCOL);
        createSend = MetaProtocolSend::new;
    }


    private class MetaProtocolSend extends AbstractProtocolSend implements MetaProtocol.Send {
        public MetaProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task createMethod() {
            return task(MessageName.CREATE_METHOD, send -> send(MessageName.CREATE_METHOD));
        }
    }
}
