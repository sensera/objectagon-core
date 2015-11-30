package org.objectagon.core.exception;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.Arrays;

/**
 * Created by christian on 2015-11-01.
 */
public class SevereError extends RuntimeException implements StandardProtocol.ErrorMessageProfile {

    private ErrorClass errorClass;
    private ErrorKind errorKind;
    private Iterable<Message.Value> params;

    public ErrorClass getErrorClass() {return errorClass;}
    public ErrorKind getErrorKind() {return errorKind;}
    public Iterable<Message.Value> getParams() {return params;}

    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.Severe;}

    public SevereError(ErrorClass errorClass, ErrorKind errorKind, Message.Value...params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = Arrays.asList(params);
    }

    public SevereError(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = params;
    }
}