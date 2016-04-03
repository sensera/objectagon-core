package org.objectagon.core.service.event;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceImpl extends AbstractService<EventServiceImpl.EventServiceWorkerImpl, StandardAddress> {

    public static ServiceName EVENT_SERVICE_ADDRESS = StandardServiceName.name("EVENT_SERVICE_ADDRESS");

    public static void registerAtServer(Server server) {
        server.registerFactory(EVENT_SERVICE_ADDRESS, EventServiceImpl::new);
    }

    private Map<String,AddressList> eventListeners = new HashMap<String, AddressList>();

    public EventServiceImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardAddress createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardAddress::standard);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EventServiceProtocol.MessageName.START_LISTEN_TO),
                (initializer, context) -> new StartListenToAction((EventServiceImpl) initializer, (EventServiceImpl.EventServiceWorkerImpl) context)
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EventServiceProtocol.MessageName.STOP_LISTEN_TO),
                (initializer, context) -> new StartListenToAction((EventServiceImpl) initializer, (EventServiceImpl.EventServiceWorkerImpl) context)
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(BroadcastEventServiceProtocol.MessageName.BROADCAST),
                (initializer, context) -> new BroadcastAction((EventServiceImpl) initializer, (EventServiceImpl.EventServiceWorkerImpl) context)
        );
    }

    void startListenTo(String name, Address address) {
        AddressList addressList = eventListeners.get(name);
        if (addressList==null) {
            addressList = new AddressList(address);
            eventListeners.put(name, addressList);
        } else
            addressList.add(address);
    }

    void stopListenTo(String name, Address address) {
        AddressList addressList = eventListeners.get(name);
        if (addressList!=null) {
            addressList.remove(address);
            if (addressList.isEmpty())
                eventListeners.remove(name);
        }
    }

    void broadcast(String name, MessageValueMessage message, EventServiceWorkerImpl serviceWorker) {
        AddressList addressList = eventListeners.get(name);
        if (addressList!=null)
            serviceWorker.broadcast(addressList, message);
    }

    @Override
    protected EventServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new EventServiceWorkerImpl(workerContext);
    }

    public class EventServiceWorkerImpl extends ServiceWorkerImpl {

        public EventServiceWorkerImpl(WorkerContext workerContext) {
            super(workerContext);
        }

        public void broadcast(Address target, MessageValueMessage message) {
            BroadcastEventServiceProtocol.Send broadcastEventServiceSend = this.createTargetSession(BroadcastEventServiceProtocol.BROADCAST_EVENT_SERVICE_PROTOCOL, target);
            broadcastEventServiceSend.broadcast(message.getMessageName(), message.getValues());
        }
    }

    private static class StartListenToAction extends StandardAction<EventServiceImpl, EventServiceWorkerImpl> {

        private Address address;
        private String name;

        public StartListenToAction(EventServiceImpl initializer, EventServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            address = context.getValue(StandardField.ADDRESS).asAddress();
            name = context.getValue(StandardField.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.startListenTo(name, address);
            return Optional.empty();
        }
    }

    private static class StopListenToAction extends StandardAction<EventServiceImpl, EventServiceWorkerImpl> {

        private Address address;
        private String name;

        public StopListenToAction(EventServiceImpl initializer, EventServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            address = context.getValue(StandardField.ADDRESS).asAddress();
            name = context.getValue(StandardField.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.stopListenTo(name, address);
            return Optional.empty();
        }
    }

    private static class BroadcastAction extends StandardAction<EventServiceImpl, EventServiceWorkerImpl> {

        private String name;
        private MessageValueMessage message;

        public BroadcastAction(EventServiceImpl initializer, EventServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            name = context.getValue(StandardField.NAME).asText();
            message = context.getValue(StandardField.MESSAGE).getValue();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.broadcast(name, message, context);
            return Optional.empty();
        }
    }
}
