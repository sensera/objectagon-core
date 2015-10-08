package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicMessage implements Message {

    private Name name;

    public Name getName() {return name;}

    public BasicMessage(Name name) {
        this.name = name;
    }
}
