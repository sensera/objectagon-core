package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-13.
 */
public interface BasicReceiver<A extends Address> extends Receiver<A> {

    Message.Field TARGET_CLASS_NAME = NamedField.text("TARGET_CLASS_NAME");
}
