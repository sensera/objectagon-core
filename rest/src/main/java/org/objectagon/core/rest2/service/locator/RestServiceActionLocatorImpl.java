package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.msg.Address;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.actions.InstanceClassProtocolRestActionsCreator;
import org.objectagon.core.rest2.service.actions.InstanceProtocolRestActionsCreator;
import org.objectagon.core.rest2.service.actions.RestActionCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 2017-01-25.
 */
public class RestServiceActionLocatorImpl implements RestServiceActionLocator {

    private List<RestActionLocator> restActionLocatorList = new ArrayList<>();

    @Override
    public RestAction locate(Address protocolSessionId, RestServiceProtocol.Method method, RestPath path) throws Exception {
        return restActionLocatorList.stream()
                .map(restActionLocator -> restActionLocator.check(method, path))
                .sorted()
                .map(RestActionMatchResult::getAction)
                .findFirst()
                .orElseThrow(() -> new Exception("Not found!"));
    }

    @Override
    public void configure(ServiceLocator serviceLocator) {
        restActionLocatorList = new ArrayList<>();
        new InstanceProtocolRestActionsCreator().create(new RestActionCreator(this, InstanceService.NAME));
        new InstanceClassProtocolRestActionsCreator().create(new RestActionCreator(this, InstanceClassService.NAME));
    }

    public void addRestAction(RestAction restAction, RestServiceProtocol.Method method, RestServiceActionLocator.RestPathPattern restPathPattern) {
        RestKeyCheck restKeyCheck = new RestKeyCheckImpl(method, restPathPattern);
        restActionLocatorList.add(new RestActionLocator(restAction, restKeyCheck));
    }

    private class RestActionLocator implements RestActionKey {
        RestAction restAction;
        RestKeyCheck restKeyCheck;


        public RestActionLocator(RestAction restAction, RestKeyCheck restKeyCheck) {
            this.restAction = restAction;
            this.restKeyCheck = restKeyCheck;
        }

        @Override
        public RestActionMatchResult check(RestServiceProtocol.Method method, RestPath path) {
            final RestMatchRating check = restKeyCheck.check(method, path);
            return new RestActionMatchResultImpl(check, restAction);
        }
    }

    private class RestActionMatchResultImpl implements RestActionMatchResult {

        RestMatchRating restMatchRating;
        RestAction action;

        public RestActionMatchResultImpl(RestMatchRating restMatchRating, RestAction action) {
            this.restMatchRating = restMatchRating;
            this.action = action;
        }

        @Override
        public RestMatchRating getRestMatchRating() {
            return restMatchRating;
        }

        @Override
        public RestAction getAction() {
            return action;
        }
    }
}
