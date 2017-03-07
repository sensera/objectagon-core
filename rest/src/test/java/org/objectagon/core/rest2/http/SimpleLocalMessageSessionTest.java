package org.objectagon.core.rest2.http;

import org.junit.Test;
import org.objectagon.core.msg.Name;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.task.Task;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2017-03-07.
 */
public class SimpleLocalMessageSessionTest {

    @Test
    public void testHappy() {
        RestServiceProtocol.SimplifiedSend send = mock(RestServiceProtocol.SimplifiedSend.class);
        Task task = mock(Task.class);

        when(send.restRequest(eq(RestServiceProtocol.Method.GET), isA(Name.class), isA(String.class), isA(List.class))).thenReturn(task);
        when(task.addFailedAction(isA(Task.FailedAction.class))).thenReturn(task);
        when(task.addSuccessAction(isA(Task.SuccessAction.class))).thenReturn(task);

        SimpleLocalMessageSession simpleLocalMessageSession = new SimpleLocalMessageSession(send);

        simpleLocalMessageSession.sendRestRequest("GET", "/home", Collections.EMPTY_LIST);

        simpleLocalMessageSession.completed();
    }

    @Test
    public void testHappyContent() {
        RestServiceProtocol.SimplifiedSend send = mock(RestServiceProtocol.SimplifiedSend.class);
        Task task = mock(Task.class);

        when(send.restRequest(eq(RestServiceProtocol.Method.POST), isA(Name.class), eq("content"), isA(List.class))).thenReturn(task);
        when(task.addFailedAction(isA(Task.FailedAction.class))).thenReturn(task);
        when(task.addSuccessAction(isA(Task.SuccessAction.class))).thenReturn(task);

        SimpleLocalMessageSession simpleLocalMessageSession = new SimpleLocalMessageSession(send);
        simpleLocalMessageSession.pushContent("content");
        simpleLocalMessageSession.sendRestRequest("POST", "/home", Collections.EMPTY_LIST);

        simpleLocalMessageSession.completed();
    }
}