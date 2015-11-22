package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-10-13.
 */
public interface BasicReceiverCtrl<R extends Receiver<A>, A extends Address> extends Receiver.ReceiverCtrl<R, A> {
}
