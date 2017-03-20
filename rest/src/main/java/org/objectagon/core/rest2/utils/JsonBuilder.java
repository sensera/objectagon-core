package org.objectagon.core.rest2.utils;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

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
        void setChildValue(Name name, String value);
        Item addChild(Name name);
    }

    interface Json {
        long size();
        void write(OutputStream outputStream) throws IOException;
        void write(ByteBuf byteBuf);
    }
}
