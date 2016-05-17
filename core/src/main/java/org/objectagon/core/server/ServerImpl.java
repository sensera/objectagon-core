package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNameValue;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.*;

import java.util.*;
import java.util.Formatter;

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
    private ServerAliasCtrlImpl serverAliasCtrl = new ServerAliasCtrlImpl();
    private org.objectagon.core.utils.Formatter.Format format = org.objectagon.core.utils.FormatterImpl.standard();
    private Formatter textFormatter = new Formatter();

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
        return createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
    }

    @Override
    public Receiver create(Receiver.ReceiverCtrl receiverCtrl) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Receiver.Configurations... configurations) {
        Factory factory = getFactoryByName(name).orElseThrow(() -> new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_NOT_FOUND, VolatileNameValue.name(name)));
        R receiver = (R) factory.create(this);
        if (receiver.getAddress()==null) {
            OneReceiverConfigurations oneReceiverConfigurations = OneReceiverConfigurations.create(Receiver.ADDRESS_CONFIGURATIONS, ReceiverAddressConfigurationParameters.create(serverId, idCounter.next(), systemTime.currentTimeMillis()));
            if (configurations.length==0) {
                receiver.configure(oneReceiverConfigurations);
            } else if (configurations.length==1) {
                receiver.configure(oneReceiverConfigurations, configurations[0]);
            } else {
                List<Receiver.Configurations> receiverConfigurations = Arrays.asList(configurations);
                receiverConfigurations.add(oneReceiverConfigurations);
                receiver.configure(receiverConfigurations.stream().toArray(Receiver.Configurations[]::new));
            }
            if (receiver.getAddress() == null)
                throw new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_HAS_NO_ADDRESS, VolatileNameValue.name(name));
            registerReceiver(receiver.getAddress(), receiver);
            //System.out.println("ServerImpl.createReceiver "+receiver.getAddress());
        }
        return receiver;
    }

    public void registerReceiver(Address address, Receiver receiver) {
        Receiver alreadyExists = receivers.put(address, receiver);
        if (alreadyExists!=null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_ALLREADY_EXISTS, VolatileAddressValue.address(address));
    }

    Optional<Transporter> processEnvelopeTarget(Address target) {
        if (target==null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.ADDRESS_IS_NULL);
        Receiver receiver = receivers.get(target);
        if (receiver==null) {
            if (target instanceof Name)
/*
            System.out.println("ServerImpl.processEnvelopeTarget >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            receivers.keySet().forEach(v -> System.out.println("ServerImpl.processEnvelopeTarget "+v));
            System.out.println("ServerImpl.processEnvelopeTarget <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
*/
            receivers.keySet().forEach(address -> {
                try {
                    address.getClass().getSimpleName();
                    target.getClass().getSimpleName();
                    address.equals(target);
/*
                    if (address != null)
                        System.out.println("ServerImpl.processEnvelopeTarget " + address.getClass().getSimpleName() + "=" + target.getClass().getSimpleName() + " => " + Objects.equals(address,target));
                    else
                        System.out.println("Address is null!");
*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
/*
            System.out.println("ServerImpl.processEnvelopeTarget ****************************************************");
*/
            return Optional.empty();
        }
        return Optional.of(receiver::receive);
    }

    @Override
    public void transport(Envelope envelope) {
        System.out.println(""+envelope);
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
                    Envelope envelope = null;
                    try {
                        while (!queue.isEmpty()) {
                            envelope = queue.poll();
                            envelope.targets(targets);
                        }
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (SevereError severeError) {
                        final Envelope errorEnvelope = envelope;
                        SwitchCase.create(severeError.getErrorKind())
                                .caseDo(ErrorKind.UNKNOWN_TARGET, errorKind -> {
                                    if (errorEnvelope != null) {
                                        transport(errorEnvelope.createReplyComposer().create(new StandardProtocol.ErrorMessage(severeError)));
                                    } else {
                                        System.out.println("EnvelopeProcessorImpl.run -------------------- SevereError ---------------------");
                                        System.out.println("Cannot find "+severeError.getValue(StandardField.ADDRESS).asAddress());
                                    }
                                })
                                .elseDo(errorKind1 -> severeError.printStackTrace());
                    }
                }
            }
        }
    }

    //  Delegation of interface Server.AliasCtrl in ReceiverCtrl
    @Override public void registerAliasForAddress(Name name, Address address) {
        serverAliasCtrl.registerAliasForAddress(name, address);
    }
    @Override public void removeAlias(Name name) {
        serverAliasCtrl.removeAlias(name);
    }
    @Override public Optional<Address> lookupAddressByAlias(Name name) {
        return serverAliasCtrl.lookupAddressByAlias(name);
    }

    private static class ReceiverAddressConfigurationParameters implements Receiver.AddressConfigurationParameters {
        private final ServerId serverId;
        private final Long id;
        private final Long timeStamp;

        private ReceiverAddressConfigurationParameters(ServerId serverId, Long id, Long timeStamp) {
            this.serverId = serverId;
            this.id = id;
            this.timeStamp = timeStamp;
        }

        public static ReceiverAddressConfigurationParameters create(ServerId serverId, Long id, Long timeStamp) {
            return new ReceiverAddressConfigurationParameters(serverId, id, timeStamp);
        }

        @Override
        public ServerId getServerId() {
            return serverId;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public Long getTimeStamp() {
            return timeStamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReceiverAddressConfigurationParameters)) return false;
            ReceiverAddressConfigurationParameters that = (ReceiverAddressConfigurationParameters) o;
            return Objects.equals(getServerId(), that.getServerId()) &&
                    Objects.equals(getId(), that.getId()) &&
                    Objects.equals(getTimeStamp(), that.getTimeStamp());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getServerId(), getId(), getTimeStamp());
        }

        @Override
        public String toString() {
            return "ReceiverAddressConfigurationParameters{" +
                    "serverId=" + serverId +
                    ", id=" + id +
                    ", timeStamp=" + timeStamp +
                    '}';
        }
    }
}
