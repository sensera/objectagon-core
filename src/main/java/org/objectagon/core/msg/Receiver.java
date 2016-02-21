package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<A extends Address> {

    A getAddress();

    void receive(Envelope envelope);

    void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<A> initializer);

    interface ReceiverCtrl extends Transporter, Server.CreateReceiverByName, Server.Factory {
        void registerFactory(Name name, Server.Factory factory);
        Server.ServerId getServerId();
    }

    interface SetInitialValues {}

    @FunctionalInterface
    interface Initializer<A extends Address> {
        <C extends SetInitialValues> C initialize(A address);
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

        Message.Value getValue(Message.Field field);

        Iterable<Message.Value> getValues();

        <R extends Receiver> R createReceiver(Name name, Initializer initializer);

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

        Transporter getTransporter();

        Address getSender();

        boolean hasName(Message.MessageName messageName);

        Message.Value getValue(Message.Field field);

        Message.MessageName getMessageName();

        Iterable<Message.Value> getValues();

        <R extends Receiver> R createReceiver(Name name, Initializer initializer);

        <U extends Protocol.Send> U createSend(Protocol.ProtocolName protocolName, Address target);

        <U extends Protocol.Reply> U createReply(Protocol.ProtocolName protocolName);

    }

}
