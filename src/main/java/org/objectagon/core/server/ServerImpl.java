package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.VolatileNameValue;

import java.util.*;

import static org.objectagon.core.msg.message.VolatileNameValue.name;

/**
 * Created by christian on 2015-11-16.
 */
public class ServerImpl implements Server, Server.Ctrl, Server.RegisterReceiver, Protocol.SessionFactory {

    private ServerId serverId;
    private Map<Name, Factory> factories = new HashMap<>();
    private Map<Address, Receiver> receivers = new HashMap<>();
    private Map<Protocol.ProtocolName, ProtocolReference> protocols = new HashMap<>();
    EnvelopeProcessor envelopeProcessor;

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
    public <U extends Protocol.Session> void registerProtocol(Protocol.ProtocolName protocolName, Protocol.Factory<U> factory) {
        protocols.put(protocolName, new ProtocolReference(serverId, protocolName, factory));
    }

    @Override
    public <U extends Protocol.Session> U createSession(Protocol.ProtocolName protocolName, Composer composer) {
        ProtocolReference<U> protocolReference = protocols.get(protocolName);
        if (protocolReference==null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.PROTOCOL_NOT_FOUND, VolatileNameValue.name(protocolName));
        return protocolReference.create(this, composer);
    }

    @Override
    public <S extends Protocol.Session> Protocol.FuncReply session(Protocol.ProtocolName protocolName, Composer composer, Protocol.Func<S> func) {
        ProtocolReference<S> protocolReference = protocols.get(protocolName);
        if (protocolReference==null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.PROTOCOL_NOT_FOUND, VolatileNameValue.name(protocolName));
        S session = protocolReference.create(this, composer);
        Protocol.FuncReply reply;
        try {
            reply = func.run(session);
        } finally {
            session.terminate();
        }
        return reply;
    }

    @Override
    public <R extends Receiver> R createReceiver(Name name) {
        Factory factory = getFactoryByName(name).orElseThrow(() -> new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_NOT_FOUND, name(name)));
        R receiver = factory.create();
        receiver.initialize();
        if (receiver.getAddress()==null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.RECEIVER_HAS_NO_ADDRESS, VolatileNameValue.name(name));
        registerReceiver(receiver.getAddress(), receiver);
        return receiver;
    }

    @Override
    public void registerReceiver(Address address, Receiver receiver) {
        receivers.put(address, receiver);
    }

    Optional<Transporter> processEnvelopeTarget(Address target) {
        Receiver receiver = receivers.get(target);
        if (receiver==null) {
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
                    while (!queue.isEmpty()) {
                        Envelope envelope = queue.poll();
                        envelope.targets(targets);
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

    private class ProtocolReference<U extends Protocol.Session>  {
        private final ServerId serverId;
        private Protocol.ProtocolName protocolName;
        private Protocol.Factory protocolFactory;
        private Protocol<U> protocol;
        private Map<Protocol.SessionId, U> sessions = new HashMap<>();

        public ProtocolReference(ServerId serverId, Protocol.ProtocolName protocolName, Protocol.Factory protocolFactory) {
            this.serverId = serverId;
            this.protocolName = protocolName;
            this.protocolFactory = protocolFactory;
        }

        public Protocol<U> getProtocol() {
            if (protocol==null)
                protocol = protocolFactory.create(serverId);
            return protocol;
        }

        public U create(Transporter transporter, Composer composer) {
            U session = getProtocol().createSession(new Protocol.SessionOwner() {
                @Override
                public Transporter getTransporter() {
                    return transporter;
                }

                @Override
                public Composer getComposer() {
                    return composer;
                }

                @Override
                public void registerReceiver(Address address, Receiver receiver) {
                    ServerImpl.this.registerReceiver(address, receiver);
                }

                @Override
                public void terminated(Protocol.SessionId session) {
                    invalidateSession(session);
                }

                @Override
                public <S extends Protocol.Session> S createSession(Protocol.ProtocolName protocolName) {
                    return ServerImpl.this.createSession(protocolName, composer);
                }
            });
            sessions.put(session.getSessionId(), session);
            return session;
        }

        private void invalidateSession(Protocol.SessionId sessionId) {
            sessions.remove(sessionId);
        }
    }
}
