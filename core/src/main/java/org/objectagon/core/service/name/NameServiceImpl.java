package org.objectagon.core.service.name;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.ForwardAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.*;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceImpl extends AbstractService<NameServiceImpl.NameServiceWorkerImpl, Service.ServiceName> {

    public static ServiceName NAME_SERVICE = StandardServiceName.name("NAME_SERVICE");

    public static void registerAtServer(Server server) {
        server.registerFactory(NAME_SERVICE, NameServiceImpl::new);
    }

    Map<Name, Address> addressByName = new HashMap<>();

    public NameServiceImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(NAME_SERVICE);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure();
        getReceiverCtrl().registerAliasForAddress(NAME_SERVICE, getAddress());  // TODO Fix this when implement start/stop server
    }

    @Override
    protected Service.ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) ->
                StandardServiceNameAddress.name(NAME_SERVICE, serverId, timestamp, addressId)
        );
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
        ).add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(NameServiceProtocol.MessageName.FORWARD),
                (initializer, context) -> new Forward((NameServiceImpl) initializer, (NameServiceImpl.NameServiceWorkerImpl) context)
        );
    }

    public Optional<Address> getAddressByName(Name name) {
        Address address = addressByName.get(name);
        if (address==null)
            return Optional.empty();
        return Optional.of(address);
    }

    public void setAddressName(Address address, Name name) throws UserException {
        //System.out.println("NameServiceImpl.setAddressName address="+address+" name="+name);
        if (addressByName.containsKey(name))
            throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_ALLREADY_REGISTERED);
        addressByName.put(name, address);
    }

    public void removeAddressName(Name name) throws UserException {
        if (addressByName.remove(name)==null)
            throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.INCONSISTENCY);
    }

    @Override
    protected NameServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new NameServiceWorkerImpl(workerContext);
    }

    public class NameServiceWorkerImpl extends ServiceWorkerImpl {

        public NameServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }

    }

    private static class RegisterNameAction extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Address address;
        private Name name;

        public RegisterNameAction(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() throws UserException {
            address = context.getValue(StandardField.ADDRESS).asAddress();
            name = context.getValue(StandardField.NAME).asName();
            if (name==null)
                throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_MISSING);
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.setAddressName(address, name);
            return Optional.empty();
        }
    }

    private static class UnregisterNameAction extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Name name;

        public UnregisterNameAction(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() throws UserException {
            name = context.getValue(StandardField.NAME).asName();
            if (name==null)
                throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_MISSING);
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.removeAddressName(name);
            return Optional.empty();
        }
    }

    private static class LookupAddressByName extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Name name;

        public LookupAddressByName(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() throws UserException {
            name = context.getValue(StandardField.NAME).asName();
            if (name==null)
                throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_MISSING);
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            Address address = initializer.getAddressByName(name)
                    .orElseThrow(() -> new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_NOT_FOUND));
            //System.out.println("LookupAddressByName.internalRun found address "+address);
            return Optional.of(VolatileAddressValue.address(address));
        }
    }

    private static class Forward extends ForwardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Name name;
        private MessageValueMessage message;

        public Forward(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() throws UserException {
            name = context.getValue(StandardField.NAME).asName();
            if (name==null)
                throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_MISSING);
            message = context.getValue(StandardField.MESSAGE).getValue();
            if (message == null)
                throw new UserException(ErrorClass.NAME_SERVICE, ErrorKind.MESSAGE_MISSING);
            return true;
        }

        @Override
        public void internalRun() throws UserException {
            Optional<Address> addressOptional = initializer.getAddressByName(name);
            //Address address = addressOptional
            //        .orElseThrow(() -> new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_NOT_FOUND));
            if (!addressOptional.isPresent())
                new UserException(ErrorClass.NAME_SERVICE, ErrorKind.NAME_NOT_FOUND);
            addressOptional.ifPresent(address -> context.forward(address, message));
        }
    }
}
