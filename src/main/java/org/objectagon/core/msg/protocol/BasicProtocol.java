package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;

/**
 * Created by christian on 2015-10-06.
 */
public interface BasicProtocol extends Protocol {

    void msgFailed();

    interface Failed extends Message {

    }
}
