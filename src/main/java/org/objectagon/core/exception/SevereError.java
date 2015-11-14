package org.objectagon.core.exception;

/**
 * Created by christian on 2015-11-01.
 */
public class SevereError extends RuntimeException {

    ErrorClass errorClass;
    ErrorKind errorKind;
    private Object[] params;

    public SevereError(ErrorClass errorClass, ErrorKind errorKind, Object...params) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = params;
    }
}
