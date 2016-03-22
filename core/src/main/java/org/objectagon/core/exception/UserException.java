package org.objectagon.core.exception;

import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.Message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-11-01.
 */
public class UserException extends Exception implements StandardProtocol.ErrorMessageProfile {

    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;
    private List<Message.Value> params;

    public StandardProtocol.ErrorClass getErrorClass() {return errorClass;}
    public StandardProtocol.ErrorKind getErrorKind() {return errorKind;}
    public Iterable<Message.Value> getParams() {return params;}
    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.User;}

    public UserException(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Message.Value... params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = Arrays.asList(params);
        if (errorKind.equals(ErrorKind.INCONSISTENCY))
            printStackTrace();
    }

    public UserException(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = new LinkedList<>();
        params.forEach(this.params::add);
        if (errorKind.equals(ErrorKind.INCONSISTENCY))
            printStackTrace();
    }
}
