package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.TransactionManagerProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.base;

/**
 * Created by christian on 2017-02-26.
 */
public interface TransactionProtocolRestActionsMap<A extends TransactionProtocolRestActionsMap.TransactionAction> extends RestActionsMap<TransactionManagerProtocol.Send, A> {

    RestPathPattern TRANSACTION_PATH                = base("transaction").build();
    RestPathPattern TRANSACTION_ID_COMMIT_PATH      = base("transaction/{id}/commit").build();
    RestPathPattern TRANSACTION_ID_ROLLBACK_PATH    = base("transaction/{id}/rollback").build();

    enum TransactionAction implements Action {
        COMMIT_TRANSACTION( RestServiceProtocol.Method.POST,     TRANSACTION_ID_COMMIT_PATH),
        ROLLBACK_TRANSACTION( RestServiceProtocol.Method.POST,     TRANSACTION_ID_ROLLBACK_PATH),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        TransactionAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL;}
    }
}
