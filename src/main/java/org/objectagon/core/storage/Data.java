package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-15.
 */
public interface Data<I extends Identity, V extends Version> extends Message.Values {
    I getIdentity();
    V getVersion();
}
