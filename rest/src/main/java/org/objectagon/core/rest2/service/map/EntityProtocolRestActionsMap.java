package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.EntityServiceProtocol;

/**
 * Created by christian on 2017-02-26.
 */
public interface EntityProtocolRestActionsMap<A extends EntityProtocolRestActionsMap.EntityAction> extends RestActionsMap<EntityServiceProtocol.Send, A> {

    enum EntityAction implements Action {
        CREATE_CLASS( RestServiceProtocol.Method.PUT,     InstanceClassProtocolRestActionsMap.CLASSES_PATH),
        CREATE_META( RestServiceProtocol.Method.PUT,      MetaProtocolRestActionsMap.META_PATH),
        //EXTEND_TRANSACTION
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        EntityAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
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
        @Override public Protocol.ProtocolName getProtocol() {return EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL;}

    }
}
