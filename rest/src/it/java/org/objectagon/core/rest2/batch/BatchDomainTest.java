package org.objectagon.core.rest2.batch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.rest2.RestServer;
import org.objectagon.core.rest2.utils.ReadStream;
import org.objectagon.core.server.LocalServerId;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BatchDomainTest {

    static int portCounter = 9900;

    private int port;
    private RestServer restServer;
    private Client client;
    private WebTarget target;
    private Invocation.Builder builder;

    @Before public void setup() {
        port = ++portCounter;
        restServer = new RestServer(LocalServerId.local("Local.Id"), port);
        client = ClientBuilder.newBuilder().newClient();
        target = client.target("http://localhost:"+port);
        //target = target.path("service").queryParam("a", "avalue");
        target = target.path("batch");
        builder = target.request();

    }

    @After public void teardown() {
        restServer.stop();
    }

    @Test public void testDomain() throws IOException {
        //Given
        String create = new String(ReadStream.readStream(getClass().getClassLoader().getResourceAsStream("create_domain.json")));
        Invocation.Builder builder = target.request();

        //When
        Response response = builder.post(Entity.entity(create, MediaType.APPLICATION_JSON_TYPE));

        //Then
        assertThat(response.getStatus(), is(200));
        assertThat(response.readEntity(String.class), is("{}"));
    }

}
