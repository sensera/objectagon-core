package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Envelope {

    interface Packager {
        Envelope create(Address target, Message message);
    }

}
