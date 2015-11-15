package org.objectagon.core.service.name;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceImpl extends AbstractService<NameServiceImpl.NameServiceWorkerImpl> implements Reactor.ActionInitializer {

    private Map<String, Address> addressByName = new HashMap<String, Address>();

    public NameServiceImpl(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(NameServiceProtocol.MessageName.REGISTER_NAME),
                (initializer, context) -> new RegisterNameAction((NameServiceImpl) initializer, (NameServiceImpl.NameServiceWorkerImpl) context)
        );
    }

    public Optional<Address> getAddressByName(String name) {
        Address address = addressByName.get(name);
        if (address==null)
            return Optional.empty();
        return Optional.of(address);
    }

    public void setAddressName(Address address, String name) throws UserException {
        if (addressByName.containsKey(name))
            throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_ALLREADY_REGISTERED);
        addressByName.put(name, address);
    }

    protected void registerName(NameServiceWorkerImpl serviceWorker) {
        Address address = serviceWorker.getValue(NameServiceProtocol.FieldName.ADDRESS).asAddress();
        String name = serviceWorker.getValue(NameServiceProtocol.FieldName.NAME).asText();
        if (addressByName.containsKey(name)) {
            serviceWorker.replyWithError(NameServiceProtocol.ErrorKind.NameAlreadyRegistered);
            return;
        }
        addressByName.put(name, address);
        serviceWorker.replyOk();
    }

    protected void unregisterName(NameServiceWorkerImpl serviceWorker) {
        String name = serviceWorker.getValue(NameServiceProtocol.FieldName.NAME).asText();
        if (!addressByName.containsKey(name)) {
            serviceWorker.replyWithError(NameServiceProtocol.ErrorKind.NameNotFound);
            return;
        }
        addressByName.remove(name);
        serviceWorker.replyOk();
    }

    protected void lookupAddressByName(NameServiceWorkerImpl serviceWorker) {
        String name = serviceWorker.getValue(NameServiceProtocol.FieldName.NAME).asText();
        Address address = addressByName.get(name);
        if (address==null) {
            serviceWorker.replyWithError(NameServiceProtocol.ErrorKind.NameNotFound);
            return;
        }
        serviceWorker.replyWithParam(new VolatileAddressValue(NameServiceProtocol.FieldName.ADDRESS, address));
    }

    @Override
    protected void handle(NameServiceWorkerImpl serviceWorker) {
        if (serviceWorker.messageHasName(NameServiceProtocol.MessageName.REGISTER_NAME)) {
            registerName(serviceWorker);
            serviceWorker.isHandled();
        } else if (serviceWorker.messageHasName(NameServiceProtocol.MessageName.UNREGISTER_NAME)) {
            unregisterName(serviceWorker);
            serviceWorker.isHandled();
        } else if (serviceWorker.messageHasName(NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME)) {
            lookupAddressByName(serviceWorker);
            serviceWorker.isHandled();
        } else
            super.handle(serviceWorker);
    }

    @Override
    protected NameServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new NameServiceWorkerImpl(workerContext);
    }

    public class NameServiceWorkerImpl extends ServiceWorkerImpl {

        NameServiceProtocol nameServiceProtocol;

        public NameServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
            nameServiceProtocol = new NameServiceProtocolImpl(workerContext.createStandardComposer(), workerContext.getTransporter());
        }


        public NameServiceProtocol.Session createNameServiceProtocolSession() {
            return nameServiceProtocol.createSession(getWorkerContext().getSender());
        }

        public void replyWithError(NameServiceProtocol.ErrorKind errorKind) {
            createNameServiceProtocolSession().replyWithError(errorKind);
        }

    }

    private static class RegisterNameAction extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Address address;
        private String name;

        public RegisterNameAction(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            address = context.getValue(NameServiceProtocol.FieldName.ADDRESS).asAddress();
            name = context.getValue(NameServiceProtocol.FieldName.NAME).asText();
            return true;
        }

        @Override
        public void run() {
            initializer.setAddressName(address, name);

            Optional<Address> addressByName = initializer.getAddressByName(name);
            if (!addressByName.isPresent()) {
                context.replyWithError(NameServiceProtocol.ErrorKind.NameAlreadyRegistered);
                return;
            }

            if (initializer.addressByName.containsKey(name)) {
                context.replyWithError(NameServiceProtocol.ErrorKind.NameAlreadyRegistered);
                return;
            }
            initializer.addressByName.put(name, address);
            context.replyOk();
        }
    }

}
