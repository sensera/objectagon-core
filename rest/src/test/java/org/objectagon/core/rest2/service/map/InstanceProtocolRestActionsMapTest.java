package org.objectagon.core.rest2.service.map;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.actions.InstanceProtocolRestActionsCreator;
import org.objectagon.core.rest2.service.actions.RestServiceActionCreator;
import org.objectagon.core.rest2.service.locator.RestPathImpl;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.storage.Identity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2017-03-13.
 */
public class InstanceProtocolRestActionsMapTest {

    private RestServiceActionLocatorImpl restServiceActionLocator;
    private Address protocolSessionId;
    private Identity addressForAlias;
    private RestServiceActionLocator.RestSession restSession;

    @Before
    public void setup() {
        restServiceActionLocator = new RestServiceActionLocatorImpl();
        new InstanceProtocolRestActionsCreator().create(new RestServiceActionCreator(restServiceActionLocator, InstanceService.NAME));

        protocolSessionId = mock(Address.class);
        addressForAlias = mock(Identity.class);
        restSession = mock(RestServiceActionLocator.RestSession.class);

        when(restSession.getIdentityByAlias(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void testPaths() throws UserException {
        verify(InstanceProtocolRestActionsMap.InstanceAction.GET_INSTANCE, "78767623", RestServiceProtocol.Method.GET, "/instance/78767623");
        verify(InstanceProtocolRestActionsMap.InstanceAction.GET_VALUE, "Item1", RestServiceProtocol.Method.GET, "/instance/Item1/field/ItemName/");
        verify(InstanceProtocolRestActionsMap.InstanceAction.ADD_RELATION, "Item2", RestServiceProtocol.Method.PUT, "/instance/Item2/relation/Arm/Body/");
        verify(InstanceProtocolRestActionsMap.InstanceAction.INVOKE_METHOD, "Item3", RestServiceProtocol.Method.PUT, "/instance/Item3/method/Walk/");
    }

    @Test(expected = UserException.class)
    public void testNoHit() throws UserException {
        restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.GET, getPath("/nopath"));
    }

    // ---------------- Support methods -----------------

    private void verify(InstanceProtocolRestActionsMap.InstanceAction action, RestServiceProtocol.Method method, String path) throws UserException {
        final RestServiceActionLocator.RestAction restAction = restServiceActionLocator.locate(protocolSessionId, method, getPath(path));

        assertEquals(action.toString(), restAction.toString());
    }

    private void verify(InstanceProtocolRestActionsMap.InstanceAction action, String alias, RestServiceProtocol.Method method, String path) throws UserException {
        final RestServiceActionLocator.RestAction restAction = restServiceActionLocator.locate(protocolSessionId, method, getPathWithAlias(path, alias, addressForAlias));

        assertEquals(action.toString(), restAction.toString());
    }

    private RestServiceActionLocator.RestPath getPath(String path) {
        return RestPathImpl.create(StandardName.name(path), restSession);
    }

    private RestServiceActionLocator.RestPath getPathWithAlias(String path, String alias, Identity addressForAlias) {
        when(restSession.getIdentityByAlias(eq(alias))).thenReturn(Optional.ofNullable(addressForAlias));
        return RestPathImpl.create(StandardName.name(path), restSession);
    }
}