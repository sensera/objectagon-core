package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<A extends Address> {

    Name ADDRESS_CONFIGURATIONS = StandardName.name("ADDRESS_CONFIGURATIONS");

    A getAddress();

    void receive(Envelope envelope);

    void configure(Configurations... configurations);

    interface ReceiverCtrl extends Transporter, Server.CreateReceiverByName, Server.Factory, Server.AliasCtrl {
        void registerFactory(Name name, Server.Factory factory);
        Server.ServerId getServerId();
    }

    interface NamedConfiguration {}

    @FunctionalInterface
    interface Configurations {
        <C extends NamedConfiguration> Optional<C> getConfigurationByName(Name name);
    }

    interface AddressConfigurationParameters extends NamedConfiguration {
        Server.ServerId getServerId();
        Long getId();
        Long getTimeStamp();
    }

    interface Worker {

        boolean isHandled();

        boolean messageHasName(Message.MessageName messageName);

        Message.MessageName getMessageName();

        void setHandled();

        void replyOk();

        void replyWithError(StandardProtocol.ErrorKind errorKind);

        void replyWithParam(Message.Value param);

        void replyWithParam(Message.MessageName message);

        void replyWithParam(Message.MessageName message, Iterable<Message.Value> values);

        void replyWithParam(Iterable<Message.Value> values);

        void failed(final StandardProtocol.ErrorClass errorClass, final StandardProtocol.ErrorKind errorKind, final Iterable<Message.Value> values);

        void success(Message.MessageName messageName, Iterable<Message.Value> values);

        Message.Value getValue(Message.Field field);

        Iterable<Message.Value> getValues();

        <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Configurations configurations);

        default StandardProtocol.ErrorMessageProfile createErrorMessageProfile(final StandardProtocol.ErrorClass errorClass, final StandardProtocol.ErrorKind errorKind, final Iterable<Message.Value> values) {
            return createErrorMessageProfile(StandardProtocol.ErrorSeverity.UNKNOWN, errorClass, errorKind, values);
        }

        default StandardProtocol.ErrorMessageProfile createErrorMessageProfile(final StandardProtocol.ErrorSeverity errorSeverity, final StandardProtocol.ErrorClass errorClass, final StandardProtocol.ErrorKind errorKind, final Iterable<Message.Value> values) {
            return new StandardProtocol.ErrorMessageProfile() {
                @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return errorSeverity;}
                @Override public StandardProtocol.ErrorClass getErrorClass() {return errorClass;}
                @Override public StandardProtocol.ErrorKind getErrorKind() {return errorKind;}
                @Override public Iterable<Message.Value> getParams() {return values;}
            };
        }
    }

    interface WorkerContext  {

        Composer createReplyToSenderComposer();

        Composer createTargetComposer(Address target);

        Composer createForwardComposer(Address target);

        Composer createRelayComposer(Service.ServiceName relayService, Name target) throws SevereError;

        Transporter getTransporter();

        Address getSender();

        boolean hasName(Message.MessageName messageName);

        Message.Value getValue(Message.Field field);

        Message.Value getHeader(Message.Field field);

        Message.MessageName getMessageName();

        Iterable<Message.Value> getValues();

        <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Configurations... configurations);

        <U extends Protocol.Send> U createSend(Protocol.ProtocolName protocolName, Address target);

        <U extends Protocol.Reply> U createReply(Protocol.ProtocolName protocolName);

        TaskBuilder getTaskBuilder();
    }


}
