package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.Collections;

/**
 * Created by christian on 2015-10-08.
 */
public class MinimalMessage extends AbstractMessage {

    public static MinimalMessage minimal(MessageName name) { return new MinimalMessage(name); }

    protected MinimalMessage(MessageName name) {
        super(name);
    }

    public Value getValue(Field field) {
        return UnknownValue.create(field);
    }

    @Override
    public Iterable<Value> getValues() {return Collections.EMPTY_LIST;}

    @Override
    public String toString() {
        return ""+getName();
    }
}
