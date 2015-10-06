package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicMessage implements Message {

    private String name;

    public String getName() {return name;}

    public Payload getPayload() {return null;}

    public BasicMessage(String name) {
        this.name = name;
    }
}
