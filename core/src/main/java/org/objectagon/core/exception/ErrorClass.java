package org.objectagon.core.exception;

import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-01.
 */
public enum ErrorClass implements StandardProtocol.ErrorClass {
    UNKNOWN, MSG, NAME_SERVICE, SERVER, ENVELOPE, INSTANCE, PROTOCOL, FIELD, INSTANCE_CLASS, RECEIVER, ENTITY, TRANSACTION, DATAVERSION, TASK, TRANSACTION_MANAGER, COMPOSER, SEARCH_SERVICE, ENTITY_SERVICE, STORAGE
}
