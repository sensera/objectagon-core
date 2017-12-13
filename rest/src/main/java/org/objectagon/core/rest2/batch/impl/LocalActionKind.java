package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.rest2.batch.BatchUpdate;

/**
 * Created by christian on 2017-04-14.
 */
public enum LocalActionKind implements BatchUpdate.ActionKind {

    // META
    CREATE_META,
    ADD_META_METHOD,
    SET_METHOD_CODE,
    SET_METHOD_NAME,
    ADD_METHOD_PARAM,

    // CLASS
    CREATE_CLASS,
    ADD_CLASS_FIELD,
    ADD_CLASS_RELATION,
    SET_FIELD_NAME,
    SET_FIELD_TYPE,
    SET_FIELD_DEFAULT_VALUE,
    SET_RELATION_NAME,
    ADD_ALIAS,
    ADD_CLASS_METHOD,
    ADD_CLASS_METHOD_PARAM,

    // INSTANCE
    CREATE_INSTANCE,
    ADD_VALUE,
    ADD_RELATION


    ;
}
