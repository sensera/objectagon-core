package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.EntityServiceProtocol;

import static org.objectagon.core.rest2.service.map.InstanceClassProtocolRestActionsMap.CLASS_ID_PATH;

/**
 * Created by christian on 2017-02-26.
 */
public interface EntityProtocolRestActionsMap<A extends EntityProtocolRestActionsMap.EntityAction> extends RestActionsMap<EntityServiceProtocol.Send, A> {

    enum EntityAction implements Action {
        GET_CLASS(RestServiceProtocol.Method.GET,               CLASS_ID_PATH, 1),
        CREATE_CLASS(RestServiceProtocol.Method.PUT,            InstanceClassProtocolRestActionsMap.CLASSES_PATH, 1),
        CREATE_META(RestServiceProtocol.Method.PUT,             MetaProtocolRestActionsMap.META_PATH, 1),
        //EXTEND_TRANSACTION
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;
        private int index;

        EntityAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern, int index) {
            this.method = method;
            this.restPathPattern = restPathPattern;
            this.index = index;
        }

        //@Override public String getName() { return name(); }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL;}
        @Override public int identityTargetAtPathIndex() {return index;}


    }
}
