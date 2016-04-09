package org.objectagon.core.exception;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2015-11-01.
 */
public class UserException extends Exception implements StandardProtocol.ErrorMessageProfile {

    Message.Field STACKTRACE = NamedField.text("STACKTRACE");

    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;
    private List<Message.Value> params = new ArrayList<>();

    public StandardProtocol.ErrorClass getErrorClass() {return errorClass;}
    public StandardProtocol.ErrorKind getErrorKind() {return errorKind;}
    public Iterable<Message.Value> getParams() {return params;}
    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.User;}

    public UserException(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Message.Value... params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params.addAll(Arrays.asList(params));
        if (errorKind.equals(ErrorKind.INCONSISTENCY) || errorKind.equals(ErrorKind.UNKNOWN_MESSAGE))
            printStackTrace();
        else
            this.params.add(MessageValue.text(STACKTRACE, stackTraceToString()));
    }

    public UserException(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = new LinkedList<>();
        params.forEach(this.params::add);
        if (errorKind.equals(ErrorKind.INCONSISTENCY) || errorKind.equals(ErrorKind.UNKNOWN_MESSAGE))
            printStackTrace();
        else
            this.params.add(MessageValue.text(STACKTRACE, stackTraceToString()));
    }

    private String stackTraceToString() {
        return Stream.of(getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining());
    }

}
