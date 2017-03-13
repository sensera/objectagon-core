package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.actions.InstanceClassProtocolRestActionsCreator;
import org.objectagon.core.rest2.service.actions.InstanceProtocolRestActionsCreator;
import org.objectagon.core.rest2.service.actions.RestActionCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by christian on 2017-01-25.
 */
public class RestServiceActionLocatorImpl implements RestServiceActionLocator {

    private final Predicate<RestActionMatchResult> onlyValidResults = restActionMatchResult -> !restActionMatchResult.getRestMatchRating().equals(RestMatchRating.None);

    private List<RestActionLocator> restActionLocatorList = new ArrayList<>();

    @Override
    public RestAction locate(Address protocolSessionId, RestServiceProtocol.Method method, RestPath path) throws UserException {
        return restActionLocatorList.stream()
                .map(restActionLocator -> restActionLocator.check(method, path))
                .filter(RestServiceActionLocator.ONLY_VALID_RESULTS)
                .peek(restActionMatchResult -> System.out.println("RestServiceActionLocatorImpl.locate "+restActionMatchResult))
                .sorted()
                .map(RestActionMatchResult::getAction)
                .findFirst()
                .orElseThrow(() -> new UserException(ErrorClass.REST_SERVICE, ErrorKind.NOT_FOUND, MessageValue.name(method), path.asValue()));
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

    private class RestActionMatchResultImpl implements RestActionMatchResult, Comparable<RestActionMatchResult> {

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

        @Override
        public int compareTo(RestActionMatchResult restActionMatchResult) {
            return restMatchRating.compareTo(restActionMatchResult.getRestMatchRating());
        }

        @Override
        public String toString() {
            return action+" "+restMatchRating;
        }
    }
}
