package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface InstanceClassProtocolRestActionsMap<A extends InstanceClassProtocolRestActionsMap.InstanceClassAction> extends RestActionsMap<InstanceClassProtocol.Send, A> {

    ParamName SET_NAME_PARAM = ParamName.create("name");

    RestPathPattern CLASSES_PATH                = urlPattern("/class/");
    RestPathPattern CLASS_ID_PATH               = urlPattern("/class/{id}/");
    RestPathPattern CLASS_NAME_PATH             = urlPattern("/class/{id}/name");
    RestPathPattern CLASS_NAME_NAME_PATH        = urlPattern("/class/{id}/name/{name}");
    RestPathPattern CLASS_INSTANCE_PATH         = urlPattern("/class/{id}/instance/");
    RestPathPattern CLASS_INSTANCE_ALIAS_PATH   = urlPattern("/class/{id}/instance/{name}/");
    RestPathPattern CLASS_FIELD_PATH            = urlPattern("/class/{id}/field/");
    RestPathPattern CLASS_RELATION_PATH         = urlPattern("/class/{id}/relation/");
    RestPathPattern CLASS_RELATION_ID_PATH      = urlPattern("/class/{id}/relation/{id}/");
    RestPathPattern CLASS_METHOD_PATH           = urlPattern("/class/{id}/method/");
    RestPathPattern CLASS_METHOD_ID_PATH        = urlPattern("/class/{id}/method/{id}/");

    enum InstanceClassAction implements Action {
        GET_NAME(RestServiceProtocol.Method.GET,           CLASS_NAME_PATH, 1),
        SET_NAME(RestServiceProtocol.Method.POST,          CLASS_NAME_NAME_PATH, 1),
        SET_NAME_PARAM(RestServiceProtocol.Method.POST,    CLASS_NAME_PATH, 1),

        CREATE_INSTANCE(RestServiceProtocol.Method.PUT,     CLASS_INSTANCE_PATH, 1),

        NAME_INSTANCE(RestServiceProtocol.Method.POST,      CLASS_INSTANCE_ALIAS_PATH, 1),
        GET_NAMED_INSTANCE(RestServiceProtocol.Method.GET,  CLASS_INSTANCE_ALIAS_PATH, 1),

        LIST_FIELDS(RestServiceProtocol.Method.GET,         CLASS_FIELD_PATH, 1),
        ADD_FIELD(RestServiceProtocol.Method.PUT,           CLASS_FIELD_PATH, 1),

        LIST_ALL_RELATIONS(RestServiceProtocol.Method.GET,  CLASS_RELATION_PATH, 1),
        LIST_RELATIONS_BY_ID(RestServiceProtocol.Method.GET,CLASS_RELATION_ID_PATH, 1),
        ADD_RELATION(RestServiceProtocol.Method.PUT,        CLASS_RELATION_ID_PATH, 1),

        LIST_METHODS(RestServiceProtocol.Method.GET,        CLASS_METHOD_PATH, 1),
        ATTACHE_METHOD(RestServiceProtocol.Method.PUT,      CLASS_METHOD_ID_PATH, 1),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;
        int identityTargetAtPathIndex;

        InstanceClassAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern, int identityTargetAtPathIndex) {
            this.method = method;
            this.restPathPattern = restPathPattern;
            this.identityTargetAtPathIndex = identityTargetAtPathIndex;
        }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL;}
        @Override public int identityTargetAtPathIndex() {return identityTargetAtPathIndex;}

    }
}
