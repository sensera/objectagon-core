package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNameValue;
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.IdCounter;
import org.objectagon.core.utils.SwitchCase;

import java.util.*;

/**
 * Created by christian on 2015-11-16.
 */
public class ServerImpl implements Server, Server.CreateReceiverByName, Receiver.ReceiverCtrl {

    private ServerId serverId;
    private SystemTime systemTime;
    private IdCounter idCounter = new IdCounter(0);
    private Map<Name, Factory> factories = new HashMap<>();
    private Map<Address, Receiver> receivers = new HashMap<>();
    EnvelopeProcessor envelopeProcessor;

    @Override public ServerId getServerId() {return serverId;}

    public ServerImpl(ServerId serverId) {
        this(serverId, System::currentTimeMillis);
    }

    public ServerImpl(ServerId serverId, SystemTime systemTime) {
        this.serverId = serverId;
        this.systemTime = systemTime;
        this.envelopeProcessor = new EnvelopeProcessorImpl(this::processEnvelopeTarget);
    }

    private Optional<Factory> getFactoryByName(Name name) {
        Factory factory = factories.get(name);
        if (factory==null)
            return Optional.empty();
        return Optional.of(factory);
    }

    @Override
    public void registerFactory(Name name, Factory factory) {
        factories.put(name, factory);
    }

    @Override
    public TaskBuilder getTaskBuilder() {
        return new StandardTaskBuilder(this);
    }

    @Override
    public Receiver create(Receiver.ReceiverCtrl receiverCtrl) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Receiver.Initializer<A> initializer) {
        Factory factory = getFactoryByName(name).orElseThrow(() -> new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_NOT_FOUND, VolatileNameValue.name(name)));
        R receiver = (R) factory.create(this);
        if (receiver.getAddress()==null) {
            receiver.initialize(serverId, systemTime.currentTimeMillis(), idCounter.next(), initializer);
            if (receiver.getAddress() == null)
                throw new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_HAS_NO_ADDRESS, VolatileNameValue.name(name));
            registerReceiver(receiver.getAddress(), receiver);
        }
        return receiver;
    }

    public void registerReceiver(Address address, Receiver receiver) {
        Receiver allreadyExists = receivers.put(address, receiver);
        if (allreadyExists!=null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_ALLREADY_EXISTS, VolatileAddressValue.address(address));
    }

    Optional<Transporter> processEnvelopeTarget(Address target) {
        Receiver receiver = receivers.get(target);
        if (receiver==null) {
            System.out.println("ServerImpl.processEnvelopeTarget >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            receivers.keySet().forEach(System.out::println);
            System.out.println("ServerImpl.processEnvelopeTarget <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            receivers.keySet().forEach(address -> System.out.println("ServerImpl.processEnvelopeTarget " + address.getClass().getSimpleName() + "=" + target.getClass().getSimpleName() + " => " + address.equals(target)));
            System.out.println("ServerImpl.processEnvelopeTarget ****************************************************");
            return Optional.empty();
        }
        return Optional.of(receiver::receive);
    }

    @Override
    public void transport(Envelope envelope) {
        System.out.println("ServerImpl.transport "+envelope);
        envelopeProcessor.transport(envelope);
    }

    private static class EnvelopeProcessorImpl implements EnvelopeProcessor {
        private Queue<Envelope> queue = new LinkedList<>();
        private Envelope.Targets targets;

        public EnvelopeProcessorImpl(Envelope.Targets targets) {
            this.targets = targets;
            new Thread(this::run).start();
        }

        @Override
        public void transport(Envelope envelope) {
            synchronized (this) {
                queue.add(envelope);
                notify();
            }
        }

        public void run() {
            synchronized (this) {
                while (true) {
                    try {
                        while (!queue.isEmpty()) {
                            Envelope envelope = queue.poll();
                            envelope.targets(targets);
                        }
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (SevereError severeError) {
                        SwitchCase.create(severeError.getErrorKind())
                                .caseDo(ErrorKind.UNKNOWN_TARGET, errorKind -> {
                                    System.out.println("EnvelopeProcessorImpl.run -------------------- SevereError ---------------------");
                                    System.out.println("Cannot find "+severeError.getValue(StandardField.ADDRESS).asAddress());

                                })
                                .elseDo(errorKind1 -> severeError.printStackTrace());
                    }
                }
            }
        }
    }


}
