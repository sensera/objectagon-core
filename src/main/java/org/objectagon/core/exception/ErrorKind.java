package org.objectagon.core.exception;

import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-01.
 */
public enum ErrorKind implements StandardProtocol.ErrorKind {
    MISSING_VERSION_IN_DATA_VERSIONS, NOT_IMPLEMENTED, NAME_ALLREADY_REGISTERED, RECEIVER_NOT_FOUND, UNKNOWN_TARGET, UNKNOWN_MESSAGE, SESSION_INVALIDATED, FIELD_NOT_FOUND, INCONSISTENCY
}