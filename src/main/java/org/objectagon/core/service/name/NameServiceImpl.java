package org.objectagon.core.service.name;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
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
public class NameServiceImpl extends AbstractService<NameServiceImpl.NameServiceWorkerImpl>  {

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
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(NameServiceProtocol.MessageName.UNREGISTER_NAME),
                (initializer, context) -> new UnregisterNameAction((NameServiceImpl) initializer, (NameServiceImpl.NameServiceWorkerImpl) context)
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME),
                (initializer, context) -> new LookupAddressByName((NameServiceImpl) initializer, (NameServiceImpl.NameServiceWorkerImpl) context)
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

    public void removeAddressName(String name) throws UserException {
        if (addressByName.remove(name)==null)
            throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.INCONSISTENCY);
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
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.setAddressName(address, name);
            return Optional.empty();
        }
    }

    private static class UnregisterNameAction extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private String name;

        public UnregisterNameAction(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            name = context.getValue(NameServiceProtocol.FieldName.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.removeAddressName(name);
            return Optional.empty();
        }
    }

    private static class LookupAddressByName extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private String name;

        public LookupAddressByName(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            name = context.getValue(NameServiceProtocol.FieldName.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            Address address = initializer.getAddressByName(name)
                    .orElseThrow(() -> new UserException(ErrorClass.NAME_SERVICE, ErrorKind.INCONSISTENCY));
            return Optional.of(new VolatileAddressValue(NameServiceProtocol.FieldName.ADDRESS, address));
        }
    }
}
