package org.objectagon.core.exception;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-11-01.
 */
public class UserException extends Exception implements StandardProtocol.ErrorMessageProfile {

    private ErrorClass errorClass;
    private ErrorKind errorKind;
    private List<Message.Value> params;

    public ErrorClass getErrorClass() {return errorClass;}
    public ErrorKind getErrorKind() {return errorKind;}
    public Iterable<Message.Value> getParams() {return params;}
    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.User;}

    public UserException(ErrorClass errorClass, ErrorKind errorKind, Message.Value... params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = Arrays.asList(params);
    }

    public UserException(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = new LinkedList<>();
        params.forEach(this.params::add);
    }
}
