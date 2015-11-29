package org.objectagon.core.msg;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.PersistenceServiceProtocol;

/**
 * Created by christian on 2015-10-06.
 */
public interface Receiver<U extends Address> {
    U getAddress();
    void receive(Envelope envelope);

    interface ReceiverCtrl<R extends Receiver<U>, U extends Address> extends Transporter, Protocol.SessionFactory {
        U createNewAddress(R receiver);
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
        default StandardProtocol.ErrorMessageProfile createErrorMessageProfile(final StandardProtocol.ErrorClass errorClass, final StandardProtocol.ErrorKind errorKind, final Iterable<Message.Value> values) {
            return createErrorMessageProfile(StandardProtocol.ErrorSeverity.Unknown, errorClass, errorKind, values);
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

    interface WorkerContext extends Protocol.SessionFactory {

        Composer createReplyToSenderComposer();

        Composer createTargetComposer(Address target);

        Transporter getTransporter();

        Address getSender();

        boolean hasName(Message.MessageName messageName);

        Message.Value getValue(Message.Field field);

        Message.MessageName getMessageName();

        Iterable<Message.Value> getValues();

    }

    public interface CtrlId extends Name {}

    @FunctionalInterface
    interface CreateNewAddress<R extends Receiver<A>, A extends Address, C extends Receiver.CtrlId> {
        A createNewAddress(Server.ServerId serverId, C ctrlId, long id);
    }

}
