package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileNameValue;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created by christian on 2015-11-16.
 */
public class ServerImpl implements Server {

    private ServerId serverId;
    private Map<Name, Factory> factories = new HashMap<>();
    private Map<Address, Receiver> receivers = new HashMap<>();
    private EnvelopeProcessor envelopeProcessor;

    @Override public ServerId getServerId() {return serverId;}

    public ServerImpl(ServerId serverId) {
        this.serverId = serverId;
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
    public <R extends Receiver> R createReceiver(Name name) {
        Factory<R> factory = getFactoryByName(name).orElseThrow(() -> new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_NOT_FOUND, new VolatileNameValue(StandardField.NAME, name)));
        return factory.create();
    }

    private Optional<Transporter> processEnvelopeTarget(Address target) {
        Receiver receiver = receivers.get(target);
        if (receiver==null)
            return Optional.empty();
        return Optional.of(receiver::receive);
    }

    @Override
    public void transport(Envelope envelope) {
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
                    while (!queue.isEmpty()) {
                        Envelope envelope = queue.poll();
                        envelope.Targets(targets);
                    }
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
