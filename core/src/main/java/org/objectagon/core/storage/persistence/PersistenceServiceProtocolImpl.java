package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
import org.objectagon.core.task.Task;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Send, Protocol.Reply> implements PersistenceServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(PERSISTENCE_SERVICE_PROTOCOL, new SingletonFactory<>(PersistenceServiceProtocolImpl::new));
    }

    public PersistenceServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, PERSISTENCE_SERVICE_PROTOCOL);
        createSend = PersistenceServiceProtocolSend::new;
    }

    private class PersistenceServiceProtocolSend extends AbstractProtocolSend implements PersistenceServiceProtocol.Send {
        public PersistenceServiceProtocolSend(CreateSendParam createSend) {
            super(createSend);
        }

        @Override
        public Task pushData(Data... datas) {
            List<Message.Value> dataList = Arrays.asList(datas).stream().map(DataMessageValue::data).collect(Collectors.toList());
            return task(
                    MessageName.PUSH_DATA,
                    send -> send.send(MessageName.PUSH_DATA, MessageValue.values(dataList)));
        }

        @Override
        public Task getData(Data.Type dataType, Identity identity, Version version) {
            return task(
                    MessageName.GET_DATA,
                    send -> send.send(MessageName.GET_DATA, MessageValue.name(dataType), MessageValue.address(identity), version.asValue()));
        }

        @Override
        public Task getLatestData(Data.Type dataType, Identity identity) {
            return task(
                    MessageName.GET_LATEST_DATA,
                    send -> send.send(MessageName.GET_LATEST_DATA, MessageValue.address(identity), MessageValue.address(identity)));
        }

        @Override
        public Task all(Identity identity) {
            return task(
                    MessageName.ALL_BY_ID,
                    send -> send.send(MessageName.ALL_BY_ID, VolatileAddressValue.address(identity)));
        }

        @Override
        public Task all(Data.Type dataType) {
            return task(
                    MessageName.ALL_BY_TYPE,
                    send -> send.send(MessageName.ALL_BY_TYPE, MessageValue.name(dataType)));
        }
    }
}
