package org.objectagon.core.exception;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.utils.Util;

import java.util.Arrays;

import static org.objectagon.core.utils.Util.getValueByField;

/**
 * Created by christian on 2015-11-01.
 */
public class SevereError extends RuntimeException implements StandardProtocol.ErrorMessageProfile {

    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;
    private Iterable<Message.Value> params;

    public StandardProtocol.ErrorClass getErrorClass() {return errorClass;}
    public StandardProtocol.ErrorKind getErrorKind() {return errorKind;}
    public Iterable<Message.Value> getParams() {return params;}
    public Message.Value getValue(Message.Field field) {return getValueByField(params, field);}

    @Override public StandardProtocol.ErrorSeverity getErrorSeverity() {return StandardProtocol.ErrorSeverity.Severe;}

    public SevereError(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Message.Value...params) {
        this(errorClass, errorKind, Arrays.asList(params));
    }

    public SevereError(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> params) {
        super("SevereError " + errorClass + " " + errorKind + paramsToString(params));
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.params = params;
        if (errorKind.equals(ErrorKind.INCONSISTENCY))
            printStackTrace();
    }

    private static String paramsToString(Iterable<Message.Value> params) {
        return Util.printValuesToString(params);
    }
}
