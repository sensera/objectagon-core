package org.objectagon.core.rest.util;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.msg.Message;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by christian on 2016-05-06.
 */
public interface JsonBuilder {

    Builder builder();

    interface Builder extends Item {
        Json build();
    }

    interface Item extends Message.BuilderItem, Message.BuilderValue {
        void setValue(String value);
        void setChildValue(String name, String value);
        Item addChild(String name);
    }

    interface Json {
        long size();
        void write(OutputStream outputStream) throws IOException;
        void write(ByteBuf byteBuf);
    }
}
