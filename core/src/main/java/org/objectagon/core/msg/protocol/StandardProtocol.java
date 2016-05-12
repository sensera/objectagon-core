package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.AbstractMessage;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.message.UnknownValue;
import org.objectagon.core.msg.message.VolatileTextValue;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-10-06.
 */
public interface StandardProtocol extends Protocol<StandardProtocol.StandardSend, StandardProtocol.StandardReply> {

    ProtocolName STANDARD_PROTOCOL = new ProtocolNameImpl("STANDARD_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        PING_MESSAGE,
        LOG_MESSAGE,
        ERROR_MESSAGE,
        OK_MESSAGE,
    }

    enum FieldName implements Message.Field {
        ERROR_SEVERITY("ERROR_SEVERITY", Message.FieldType.Text),
        ERROR_CLASS("ERROR_CLASS", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text),
        PARAM("PARAM", Message.FieldType.Any),
        ORIGINAL_MESSAGE("ORIGINAL_MESSAGE", Message.FieldType.MessageName),
        ;

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        @Override
        public boolean sameField(Message.Value value) {
            return equals(value.getField());
        }

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }
    }

    interface StandardReply extends Protocol.Reply {
        void replyWithError(ErrorClass errorClass, ErrorKind errorKind);
        void replyWithError(ErrorKind errorKind);
        void replyWithError(ErrorMessageProfile errorMessageProfile);
    }

    interface StandardSend extends Protocol.Send {
        Task ping(Address target);
        Task log(Address target, Iterable<Message.Value> params);
    }

    class ErrorMessage extends AbstractMessage {
        private String errorSeverity;
        private String errorClass;
        private String errorKind;
        private final List<Value> values;

        public ErrorMessage(ErrorMessageProfile errorMessageProfile) {
            this(errorMessageProfile.getErrorSeverity(), errorMessageProfile.getErrorClass(), errorMessageProfile.getErrorKind(), errorMessageProfile.getParams());
        }

        public ErrorMessage(ErrorSeverity errorSeverity, ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values) {
            this(errorSeverity.name(), errorClass.name(), errorKind.name(), values);
        }

        public ErrorMessage(ErrorSeverity errorSeverity, ErrorClass errorClass, ErrorKind errorKind, Value...values) {
            this(errorSeverity.name(), errorClass.name(), errorKind.name(), values);
        }

        public ErrorMessage(String errorSeverity, String errorClass, String errorKind, Value...values) {
            super(StandardProtocol.MessageName.ERROR_MESSAGE);
            this.errorSeverity = errorSeverity;
            this.errorClass = errorClass;
            this.errorKind = errorKind;
            this.values = Arrays.asList(values);
        }

        public ErrorMessage(String errorSeverity, String errorClass, String errorKind, Iterable<Message.Value> values) {
            super(StandardProtocol.MessageName.ERROR_MESSAGE);
            this.errorSeverity = errorSeverity;
            this.errorClass = errorClass;
            this.errorKind = errorKind;
            this.values = new LinkedList<>();
            values.forEach(this.values::add);
        }

        @Override
        public Iterable<Value> getValues() {
            List<Value> newList = new ArrayList<>(values);
            newList.add(getValue(StandardProtocol.FieldName.ERROR_SEVERITY));
            newList.add(getValue(StandardProtocol.FieldName.ERROR_CLASS));
            newList.add(getValue(StandardProtocol.FieldName.ERROR_KIND));
            return newList;
        }

        public Value getValue(Field field) {
            if (field.equals(StandardProtocol.FieldName.ERROR_SEVERITY))
                return VolatileTextValue.text(field, errorSeverity);
            if (field.equals(StandardProtocol.FieldName.ERROR_CLASS))
                return VolatileTextValue.text(field, errorClass);
            else if (field.equals(StandardProtocol.FieldName.ERROR_KIND))
                return VolatileTextValue.text(field, errorKind);
            return lookupValueInParamsByField(values, field).orElse(UnknownValue.create(field));
        }

        @Override
        public String toString() {
            return "Error "+errorSeverity+" "+errorClass+" "+errorKind + Util.printValuesToString(getValues());
        }

        public static Message error(org.objectagon.core.exception.ErrorKind errorKind) {
            return new ErrorMessage(ErrorSeverity.UNKNOWN, org.objectagon.core.exception.ErrorClass.UNKNOWN, errorKind);
        }

    }

    enum ErrorSeverity {
        Critical,
        Severe,
        User,
        UNKNOWN
    }

    interface ErrorClass {
        String name();
    }
    interface ErrorKind {
        String name();
    }

    interface ErrorMessageProfile {
        ErrorSeverity getErrorSeverity();
        StandardProtocol.ErrorClass getErrorClass();
        StandardProtocol.ErrorKind getErrorKind();
        Iterable<Message.Value> getParams();
    }

}
