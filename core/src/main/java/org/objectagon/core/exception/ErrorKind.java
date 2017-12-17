package org.objectagon.core.exception;

import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-01.
 */
public enum ErrorKind implements StandardProtocol.ErrorKind {
    MISSING_VERSION_IN_DATA_VERSIONS, NOT_IMPLEMENTED, NAME_ALLREADY_REGISTERED, RECEIVER_NOT_FOUND, UNKNOWN_TARGET, UNKNOWN_MESSAGE, SESSION_INVALIDATED, FIELD_NOT_FOUND, ILLEGAL_PROTOCOL, RECEIVER_HAS_NO_ADDRESS, PROTOCOL_NOT_FOUND, TIMEOUT, NAME_MISSING, WRONG_FIELD, RELATION_NOT_FOUND, ACTIVE_BRANCH_IN_ROLLBACK, TRANSACTION_NOT_FOUND, ROOT_TRANSACTION_PRESENT, TRANSACTION_HAS_CHANGES, ROOT_IS_NOT_EMPTY, RECEIVER_ALLREADY_EXISTS, NAME_NOT_FOUND, ADDRESS_IS_NULL, TARGET_IS_NULL, SENDER_IS_NULL, MESSAGE_MISSING, TRANSACTION_ALREADY_PRESENT, MISSING_CONFIGURATION, MISSING_PATH_ITEM, TEXT_CONVERT_NOT_SUPPORTED_BY_FIELD, INSTANCE_NOT_FOUND, FAILED_TO_INVOKE_METHOD, UNKNOWN, NOT_FOUND, ALIAS_NOT_FOUND, UNABLE_TO_COMPLETE_TASK, UNEXPECTED, TYPE_MISSING, PARSE_ERROR, CAST, INCONSISTENCY
}
