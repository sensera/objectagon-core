package org.objectagon.core.rest;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public interface RestProcessor {

    enum Operation {Get, SaveNew, UpdateExecute, Delete}

    void process(ServerCore serverCore, Request request, Response response);

    interface Request {
        Operation getOperation();
        Optional<PathItem> getPathItem(int index);
        String getUser();
        Message.Value[] queryAsValues();
        RequestValue getValue(Message.Field field);
        Optional<Address> getAlias();
    }

    interface Response {
        JsonBuilder createJsonBuilder(String name);

        void error(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values);
        void reply(Message.MessageName messageName, Iterable<Message.Value> values);
    }

    interface PathItem {
        <A extends Address> A address(Message.Field field);
        <N extends Name> N name(Message.Field field);
    }

    interface JsonBuilder {
        JsonBuilder addValue(String value);
        JsonBuilder addChild(String name);
        void completed();
    }

    interface RequestValue {
        <A extends Address> A address();
        <N extends Name> N name();
        String text();
    }
}


