package org.objectagon.core.rest2.service.locator;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Address;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2017-03-07.
 */
public class RestServiceActionLocatorImplTest {

    private RestServiceActionLocatorImpl restServiceActionLocator;
    private RestServiceActionLocator.RestAction restAction;
    private RestServiceActionLocator.RestAction restAction2;
    private RestServiceActionLocator.RestPathPattern restPathPattern;
    private RestServiceActionLocator.RestPathPattern restPathPattern2;
    private Address protocolSessionId;
    private RestServiceActionLocator.RestPath restPath;

    @Before
    public void setup() {
        restServiceActionLocator = new RestServiceActionLocatorImpl();

        restAction = mock(RestServiceActionLocator.RestAction.class);
        restAction2 = mock(RestServiceActionLocator.RestAction.class);
        restPathPattern = mock(RestServiceActionLocator.RestPathPattern.class);
        restPathPattern2 = mock(RestServiceActionLocator.RestPathPattern.class);

        protocolSessionId = mock(Address.class);
        restPath = mock(RestServiceActionLocator.RestPath.class);
    }

    @Test
    public void testHappyPath() throws Exception {
        restServiceActionLocator.addRestAction(restAction, RestServiceProtocol.Method.GET, restPathPattern);
        when(restPathPattern.check(eq(restPath))).thenReturn(RestServiceActionLocator.RestMatchRating.Perfect);

        final RestServiceActionLocator.RestAction locatedAction = restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.GET, restPath);

        assertEquals(restAction, locatedAction);
    }

    @Test(expected = Exception.class)
    public void testWrongMethod() throws Exception {
        restServiceActionLocator.addRestAction(restAction, RestServiceProtocol.Method.GET, restPathPattern);
        when(restPathPattern.check(eq(restPath))).thenReturn(RestServiceActionLocator.RestMatchRating.Perfect);

        restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.POST, restPath);
    }

    @Test(expected = Exception.class)
    public void testNoMatch() throws Exception {
        restServiceActionLocator.addRestAction(restAction, RestServiceProtocol.Method.GET, restPathPattern);
        when(restPathPattern.check(eq(restPath))).thenReturn(RestServiceActionLocator.RestMatchRating.None);

        restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.GET, restPath);
    }

    @Test
    public void testSortedResults() throws Exception {
        restServiceActionLocator.addRestAction(restAction, RestServiceProtocol.Method.GET, restPathPattern);
        restServiceActionLocator.addRestAction(restAction2, RestServiceProtocol.Method.GET, restPathPattern2);
        when(restPathPattern.check(eq(restPath))).thenReturn(RestServiceActionLocator.RestMatchRating.Ok);
        when(restPathPattern2.check(eq(restPath))).thenReturn(RestServiceActionLocator.RestMatchRating.Perfect);

        final RestServiceActionLocator.RestAction locatedAction = restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.GET, restPath);

        assertEquals(restAction2, locatedAction);
    }
}