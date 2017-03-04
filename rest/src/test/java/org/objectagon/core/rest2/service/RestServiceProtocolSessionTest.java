package org.objectagon.core.rest2.service;

import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.name.StandardName;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by christian on 2017-01-25.
 */
public class RestServiceProtocolSessionTest extends AbstractProtocolSessionTest<RestServiceProtocol.Send> {

    @Override
    public RestServiceProtocol.Send createProtocolSend(Receiver.ReceiverCtrl receiverCtrl, Protocol.CreateSendParam createSendParam) {
        RestServiceProtocol restServiceProtocol = new RestServiceProtocolImpl(receiverCtrl);
        return restServiceProtocol.createSend(createSendParam);
    }

    @Test
    @Ignore("WIP: REST SERVER")
    public void testRestRequest() {
        final StandardName path = StandardName.name("Path");
        final List params = Collections.EMPTY_LIST;

        startTaskAndVerifySentEvelope(
                send.restRequest(RestServiceProtocol.Method.GET, path, params),
                message -> {
                    assertEquals(message.getName(), RestServiceProtocol.MessageName.REST_REQUEST);
                    assertEquals(message.getValue(RestServiceProtocol.METHOD_FIELD).asName(), RestServiceProtocol.Method.GET);
                    assertEquals(message.getValue(RestServiceProtocol.PATH_FIELD).asName(), path);
                    assertEquals(message.getValue(RestServiceProtocol.PARAMS_FIELD).asMap(), new HashMap<Name, Message.Value>());
            });
    }
}