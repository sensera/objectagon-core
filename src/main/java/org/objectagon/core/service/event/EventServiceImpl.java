package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.ReceiverCtrlIdName;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.server.StandardFactory;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.objectagon.core.utils.Util.concat;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceImpl extends AbstractService<EventServiceImpl.EventServiceWorkerImpl, StandardAddress, Receiver.CreateNewAddressParams> {

    public static NamedAddress EVENT_SERVICE_ADDRESS = new NamedAddress(EventServiceProtocol.EVENT_SERVICE_PROTOCOL);

    public static ReceiverCtrlIdName EVENT_SERVICE_CTRL_ID_NAME = new ReceiverCtrlIdName("EventService");

    public static void registerAtServer(Server.Ctrl server) {
        StandardFactory<EventServiceImpl, StandardAddress, ReceiverCtrlIdName, CreateNewAddressParams> eventServiceStandardAddressReceiverCtrlIdNameStandardFactory =
                StandardFactory.create(server, EVENT_SERVICE_CTRL_ID_NAME, StandardAddress::standard, EventServiceImpl::new);
        server.registerFactory(EVENT_SERVICE_CTRL_ID_NAME, eventServiceStandardAddressReceiverCtrlIdNameStandardFactory);
    }

    public static Server.Factory factory(Server.Ctrl server) {
        StandardFactory<EventServiceImpl, StandardAddress, ReceiverCtrlIdName, CreateNewAddressParams> eventServiceStandardAddressReceiverCtrlIdNameStandardFactory =
                StandardFactory.create(server, EVENT_SERVICE_CTRL_ID_NAME, StandardAddress::standard, EventServiceImpl::new);
        return eventServiceStandardAddressReceiverCtrlIdNameStandardFactory;
    }

    private Map<String,AddressList> eventListeners = new HashMap<String, AddressList>();

    @Override protected CreateNewAddressParams createNewAddressParams() {return null;}

    public EventServiceImpl(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
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
        if (addressList!=null)
            addressList.remove(address);
        if (addressList.isEmpty())
            eventListeners.remove(name);
    }

    void broadcast(String name, Message.MessageName message, EventServiceWorkerImpl serviceWorker) {
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

        public void broadcast(Address target, Message.MessageName message, Message.Value... values) {
            this.<BroadcastEventServiceProtocol.Session>createTargetSession(BroadcastEventServiceProtocol.BROADCAST_EVENT_SERVICE_PROTOCOL, target).broadcast(message, concat(values));
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

        private Message.MessageName message ;
        private String name;

        public BroadcastAction(EventServiceImpl initializer, EventServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            message = context.getValue(StandardField.MESSAGE).asMessage();
            name = context.getValue(StandardField.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.broadcast(name, message, context);
            return Optional.empty();
        }
    }
}
