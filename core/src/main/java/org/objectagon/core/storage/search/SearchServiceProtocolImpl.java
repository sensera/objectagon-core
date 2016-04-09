package org.objectagon.core.storage.search;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.SearchServiceProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-04-09.
 */
public class SearchServiceProtocolImpl extends AbstractProtocol<SearchServiceProtocol.Send, Protocol.Reply> implements SearchServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(SEARCH_SERVICE_PROTOCOL, new SingletonFactory<>(SearchServiceProtocolImpl::new));
    }

    public SearchServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, SEARCH_SERVICE_PROTOCOL);
        createSend = SearchServiceProtocolSend::new;
    }

    private class SearchServiceProtocolSend extends AbstractProtocolSend implements SearchServiceProtocol.Send {
        public SearchServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task nameSearch(Name name) {
            return task(MessageName.NAME_SEARCH, send -> send.send(MessageName.NAME_SEARCH, MessageValue.name(name)));
        }
    }
}
