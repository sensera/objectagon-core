package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.object.meta.MetaService;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.actions.*;
import org.objectagon.core.storage.EntityProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.storage.transaction.TransactionService;

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
        new InstanceProtocolRestActionsCreator().create(new RestActionCreator<InstanceProtocol.Send>(this, InstanceService.NAME));
        new InstanceClassProtocolRestActionsCreator().create(new RestActionCreator<InstanceClassProtocol.Send>(this, InstanceClassService.NAME));
        new TransactionProtocolRestActionsCreator().create(new RestActionCreator<TransactionServiceProtocol.Send>(this, TransactionService.NAME));
        new EntityInstanceClassProtocolRestActionsCreator().create(new RestActionCreator<EntityProtocol.Send>(this, InstanceClassService.NAME));
        new EntityMetaProtocolRestActionsCreator().create(new RestActionCreator<EntityProtocol.Send>(this, MetaService.NAME));
        new MetaProtocolRestActionsCreator().create(new RestActionCreator<EntityProtocol.Send>(this, MetaService.NAME));
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
