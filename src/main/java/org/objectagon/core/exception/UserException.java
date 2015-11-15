package org.objectagon.core.exception;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-01.
 */
public class UserException extends Exception implements StandardProtocol.ErrorMessageProfile {

    ErrorClass errorClass;
    ErrorKind errorKind;
    private Message.Value[] params;

    public ErrorClass getErrorClass() {return errorClass;}
    public ErrorKind getErrorKind() {return errorKind;}
    public Message.Value[] getParams() {return params;}
    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.User;}

    public UserException(ErrorClass errorClass, ErrorKind errorKind, Message.Value... params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = params;
    }
}
