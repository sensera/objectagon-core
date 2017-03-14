package org.objectagon.core.rest2.service;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.OneReceiverConfigurations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2017-03-05.
 */
public class RestServiceTest extends AbstractServiceTest<RestService> {

    RestServiceActionLocator restServiceActionLocator;
    private final RestServiceProtocol.Method method = RestServiceProtocol.Method.GET;
    private final StandardName path = StandardName.name("/");
    private final String content = "content";
    private RestServiceActionLocator.RestAction restAction;
    private Task restTask = mock(Task.class);

    @Before
    public void setUp() throws Exception {
        restServiceActionLocator = mock(RestServiceActionLocator.class);
        restAction =  mock(RestServiceActionLocator.RestAction.class);
        restTask = mock(Task.class);
        when(restServiceActionLocator.locate(isA(Address.class), eq(method), isA(RestServiceActionLocator.RestPath.class))).thenReturn(restAction);
        when(restServiceActionLocator.locate(isNull(Address.class), eq(method), isA(RestServiceActionLocator.RestPath.class))).thenReturn(restAction);
        when(restAction.createTask(isA(TaskBuilder.class), isA(RestServiceActionLocator.IdentityStore.class), isA(RestServiceActionLocator.RestPath.class), any(), eq(content))).thenReturn(restTask);
        when(restAction.createTask(isNull(TaskBuilder.class), isA(RestServiceActionLocator.IdentityStore.class), isA(RestServiceActionLocator.RestPath.class), any(), eq(content))).thenReturn(restTask);
        when(restTask.addSuccessAction(isA(Task.SuccessAction.class))).thenReturn(restTask);
        when(restTask.addFailedAction(isA(Task.FailedAction.class))).thenReturn(restTask);
        super.setup();
    }

    @Override
    protected RestService createTargetService() {
        return new RestService(receiverCtrl);
    }

    @Override
    protected void configureTargetService() {
        targetService.configure(getAddressReceiverConfigurations(),
                OneReceiverConfigurations.create(RestService.REST_SERVICE_CONFIGURATION_NAME,
                (RestService.RestServiceConfig) () -> restServiceActionLocator)
        );
    }

    @Test
    public void testSimpleRequest() {
        //Task restRequest(Method method, Name path, String content, List<KeyValue<ParamName, Message.Value>> params);
        targetService.receive(createStandardEnvelope(SimpleMessage.simple(RestServiceProtocol.MessageName.SIMPLE_REST_CONTENT,
                MessageValue.name(RestServiceProtocol.METHOD_FIELD, method),
                MessageValue.name(RestServiceProtocol.PATH_FIELD, path),
                MessageValue.text(RestServiceProtocol.CONTENT_FIELD, content)
        )));

        verify(restTask, times(1)).start();
    }
}
