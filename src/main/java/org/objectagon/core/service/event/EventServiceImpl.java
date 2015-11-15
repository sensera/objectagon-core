package org.objectagon.core.service.event;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceImpl extends AbstractService<EventServiceImpl.EventServiceWorkerImpl> {

    private Map<String,AddressList> eventListeners = new HashMap<String, AddressList>();

    public EventServiceImpl(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(EventServiceProtocol.MessageName.START_LISTEN_TO),
                (initializer, context) -> new StartListenToAction((EventServiceImpl) initializer, (EventServiceImpl.EventServiceWorkerImpl) context)
        );
    }

    protected void startListenTo(EventServiceWorkerImpl serviceWorker) {
        Address address = serviceWorker.getValue(EventServiceProtocol.FieldName.ADDRESS).asAddress();
        String name = serviceWorker.getValue(EventServiceProtocol.FieldName.NAME).asText();

        AddressList addressList = eventListeners.get(name);
        if (addressList==null)
            addressList = new AddressList(address);
        else
            addressList.add(address);

        serviceWorker.replyOk();
    }

    protected void stopListenTo(EventServiceWorkerImpl serviceWorker) {
        Address address = serviceWorker.getValue(EventServiceProtocol.FieldName.ADDRESS).asAddress();
        String name = serviceWorker.getValue(EventServiceProtocol.FieldName.NAME).asText();

        AddressList addressList = eventListeners.get(name);
        if (addressList!=null)
            addressList.remove(address);

        serviceWorker.replyOk();
    }

    protected void broadcast(EventServiceWorkerImpl serviceWorker) {
        String name = serviceWorker.getValue(EventServiceProtocol.FieldName.NAME).asText();
        Message message = serviceWorker.getValue(EventServiceProtocol.FieldName.MESSAGE).asMessage();

        AddressList addressList = eventListeners.get(name);
        if (addressList!=null)
            serviceWorker.broadcast(message, addressList);

        serviceWorker.replyOk();
    }

    @Override
    protected void handle(EventServiceWorkerImpl serviceWorker) {
        if (serviceWorker.messageHasName(EventServiceProtocol.MessageName.START_LISTEN_TO)) {
            startListenTo(serviceWorker);
            serviceWorker.setHandled();
        } else if (serviceWorker.messageHasName(EventServiceProtocol.MessageName.STOP_LISTEN_TO)) {
            stopListenTo(serviceWorker);
            serviceWorker.setHandled();
        } else if (serviceWorker.messageHasName(EventServiceProtocol.MessageName.BROADCAST)) {
            broadcast(serviceWorker);
            serviceWorker.setHandled();
        } else
            super.handle(serviceWorker);
    }

    @Override
    protected EventServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new EventServiceWorkerImpl(workerContext);
    }

    public class EventServiceWorkerImpl extends ServiceWorkerImpl {

        EventServiceProtocol eventServiceProtocol;

        public EventServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
            eventServiceProtocol = new EventServiceProtocolImpl(workerContext.createStandardComposer(), workerContext.getTransporter());
        }


        public EventServiceProtocol.Session createEventServiceProtocolSession() {
            return eventServiceProtocol.createSession(getWorkerContext().getSender());
        }

        public EventServiceProtocol.Session createEventServiceProtocolSession(Address address) {
            return eventServiceProtocol.createSession(address);
        }

        public void replyWithError(EventServiceProtocol.ErrorKind errorKind) {
            createEventServiceProtocolSession().replyWithError(errorKind);
        }

        public void broadcast(Message message, Address target) {
            createEventServiceProtocolSession(target).broadcast(message);
        }
    }

    private static class StartListenToAction extends StandardAction<EventServiceImpl, EventServiceWorkerImpl> {

        private Address address;
        private String name;

        public StartListenToAction(EventServiceImpl initializer, EventServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            address = context.getValue(EventServiceProtocol.FieldName.ADDRESS).asAddress();
            name = context.getValue(EventServiceProtocol.FieldName.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.setAddressName(address, name);
            return Optional.empty();
        }
    }


}
