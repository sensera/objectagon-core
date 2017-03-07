package org.objectagon.core.rest2.http;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Address;
import org.objectagon.core.rest2.service.AbstractServiceTest;
import org.objectagon.core.utils.OneReceiverConfigurations;

import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-03-05.
 */
public class HttpServiceTest extends AbstractServiceTest<HttpService> {

    Address restServiceAddress;
    HttpService.HttpServer httpServer;
    private HttpCommunicator httpCommunicator;

    @Before
    public void setup() {
        restServiceAddress = mock(Address.class);
        httpServer = mock(HttpService.HttpServer.class);
        super.setup();
    }

    @Override
    protected HttpService createTargetService() {
        return new HttpService(receiverCtrl);
    }

    @Override
    protected void configureTargetService() {
        targetService.configure(getAddressReceiverConfigurations(), OneReceiverConfigurations.create(HttpService.HTTP_SERVICE_CONFIGURATION_NAME, new HttpService.HttpServiceConfig() {
            @Override public Address getRestServiceAddress() {return restServiceAddress;}
            @Override public int getListenPort() {return 80;}
            @Override public HttpService.CreateHttpServer getCreateHttpServer() {return (httpCommunicator, port) -> storeHttpCommunicatorAndReturnMockedHttpServer(httpCommunicator);}
        }));
    }

    private HttpService.HttpServer storeHttpCommunicatorAndReturnMockedHttpServer(HttpCommunicator httpCommunicator) {
        this.httpCommunicator = httpCommunicator;
        return httpServer;
    }

    @Test
    public void testRequest() {
        //httpCommunicator.sendMessageWithAsyncContent("GET","/home", Collections.EMPTY_LIST);
    }

}