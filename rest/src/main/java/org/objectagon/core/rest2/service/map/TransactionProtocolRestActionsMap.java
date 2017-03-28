package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.base;

/**
 * Created by christian on 2017-02-26.
 */
public interface TransactionProtocolRestActionsMap<A extends TransactionProtocolRestActionsMap.TransactionAction> extends RestActionsMap<TransactionServiceProtocol.Send, A> {

    RestPathPattern TRANSACTION_PATH            = base("transaction").build();

    enum TransactionAction implements Action {
        CREATE_TRANSACTION( RestServiceProtocol.Method.GET,     TRANSACTION_PATH),
        //EXTEND_TRANSACTION
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        TransactionAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        //@Override public String getName() { return name(); }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return TransactionServiceProtocol.TRANSACTION_SERVICE_PROTOCOL;}
    }
}
