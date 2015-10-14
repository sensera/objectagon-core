package org.objectagon.core.service.name;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceImpl extends AbstractService<NameServiceImpl.NameServiceWorkerImpl>  {

    private Map<String, Address> addressByName = new HashMap<String, Address>();

    public NameServiceImpl(BasicReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
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

}
