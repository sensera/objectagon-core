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
import org.objectagon.core.rest2.service.actions.RestActionCreator;
import org.objectagon.core.rest2.service.locator.RestPathImpl;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.storage.Identity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-03-13.
 */
public class InstanceProtocolRestActionsMapTest {

    private RestServiceActionLocatorImpl restServiceActionLocator;
    private Address protocolSessionId;
    private Identity addressForAlias;

    @Before
    public void setup() {
        restServiceActionLocator = new RestServiceActionLocatorImpl();
        new InstanceProtocolRestActionsCreator().create(new RestActionCreator(restServiceActionLocator, InstanceService.NAME));

        protocolSessionId = mock(Address.class);
        addressForAlias = mock(Identity.class);
    }

    @Test
    public void testPaths() throws UserException {
        verify(InstanceProtocolRestActionsMap.InstanceAction.LIST_INSTANCES, RestServiceProtocol.Method.GET, "/instance");
        verify(InstanceProtocolRestActionsMap.InstanceAction.LIST_INSTANCES, RestServiceProtocol.Method.GET, "/instance/");
        verify(InstanceProtocolRestActionsMap.InstanceAction.CREATE_INSTANCE, "Item", RestServiceProtocol.Method.PUT, "/instance/Item");
        verify(InstanceProtocolRestActionsMap.InstanceAction.GET_INSTANCE, "78767623", RestServiceProtocol.Method.GET, "/instance/78767623");
    }


    @Test(expected = UserException.class)
    public void testNoHit() throws UserException {
        final RestServiceActionLocator.RestAction restAction = restServiceActionLocator.locate(protocolSessionId, RestServiceProtocol.Method.GET, getPath("/nopath"));
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
        return RestPathImpl.create(StandardName.name(path), identityAlias -> Optional.empty());
    }

    private RestServiceActionLocator.RestPath getPathWithAlias(String path, String alias, Identity addressForAlias) {
        return RestPathImpl.create(StandardName.name(path), identityAlias -> identityAlias.equals(alias) ? Optional.of(addressForAlias) : Optional.empty());
    }
}