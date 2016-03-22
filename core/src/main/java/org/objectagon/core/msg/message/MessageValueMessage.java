package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;
import lombok.Data;

/**
 * Created by christian on 2016-03-06.
 */
@Data
public class MessageValueMessage {
    private final Message.MessageName messageName;
    private final Iterable<Message.Value> values;

}
