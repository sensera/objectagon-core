package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.TransactionManagerProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface TransactionProtocolRestActionsMap<A extends TransactionProtocolRestActionsMap.TransactionAction> extends RestActionsMap<TransactionManagerProtocol.Send, A> {

    RestPathPattern TRANSACTION_PATH                = urlPattern("transaction");
    RestPathPattern TRANSACTION_ID_COMMIT_PATH      = urlPattern("transaction/{id}/commit");
    RestPathPattern TRANSACTION_ID_ROLLBACK_PATH    = urlPattern("transaction/{id}/rollback");
    RestPathPattern TRANSACTION_ID_EXTEND_PATH      = urlPattern("transaction/{id}/extend");
    RestPathPattern TRANSACTION_ID_ASSIGN_PATH      = urlPattern("transaction/{id}/assign");

    enum TransactionAction implements Action {
        COMMIT_TRANSACTION(     RestServiceProtocol.Method.GET,         TRANSACTION_ID_COMMIT_PATH),
        ROLLBACK_TRANSACTION(   RestServiceProtocol.Method.GET,         TRANSACTION_ID_ROLLBACK_PATH),
        EXTEND_TRANSACTION(     RestServiceProtocol.Method.GET,         TRANSACTION_ID_EXTEND_PATH),
        ASSIGN_TRANSACTION(     RestServiceProtocol.Method.GET,         TRANSACTION_ID_ASSIGN_PATH),
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
