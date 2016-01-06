package org.objectagon.core.service.name;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.server.SingletonFactory;
import org.objectagon.core.server.StandardFactory;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceImpl extends AbstractService<NameServiceImpl.NameServiceWorkerImpl, StandardAddress, Receiver.CreateNewAddressParams>  {

    public static NamedAddress NAME_SERVICE_ADDRESS = new NamedAddress(NameServiceProtocol.NAME_SERVICE_PROTOCOL);

    public static ReceiverCtrlIdName NAME_SERVICE_CTRL_NAME = new ReceiverCtrlIdName("NameService");

    public static void registerAtServer(Server.Ctrl server) {
        server.registerFactory(NAME_SERVICE_CTRL_NAME,
                new SingletonFactory<ReceiverCtrlIdName, Address>(server, NAME_SERVICE_CTRL_NAME, NAME_SERVICE_ADDRESS, NameServiceImpl::new)
        );
    }

    private Map<String, Address> addressByName = new HashMap<String, Address>();

    public NameServiceImpl(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override protected Receiver.CreateNewAddressParams createNewAddressParams() {return null;}

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
        //System.out.println("NameServiceImpl.setAddressName address="+address+" name="+name);
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

        public NameServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }
    }

    private static class RegisterNameAction extends StandardAction<NameServiceImpl, NameServiceWorkerImpl> {

        private Address address;
        private String name;

        public RegisterNameAction(NameServiceImpl initializer, NameServiceWorkerImpl context) {
            super(initializer, context);
        }

        public boolean initialize() {
            address = context.getValue(StandardField.ADDRESS).asAddress();
            name = context.getValue(StandardField.NAME).asText();
            //System.out.println("RegisterNameAction.initialize "+address+" "+name);
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
            name = context.getValue(StandardField.NAME).asText();
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
            name = context.getValue(StandardField.NAME).asText();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            Address address = initializer.getAddressByName(name)
                    .orElseThrow(() -> new UserException(ErrorClass.NAME_SERVICE, ErrorKind.INCONSISTENCY));
            //System.out.println("LookupAddressByName.internalRun found address "+address);
            return Optional.of(address(address));
        }
    }
}
