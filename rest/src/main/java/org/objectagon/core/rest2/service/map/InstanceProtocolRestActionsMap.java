package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface InstanceProtocolRestActionsMap<A extends InstanceProtocolRestActionsMap.InstanceAction> extends RestActionsMap<InstanceProtocol.Send, A> {

    //RestPathPattern INSTANCES_PATH                  = urlPattern("/instance/");
    RestPathPattern INSTANCE_ID_PATH                = urlPattern("/instance/{id}/");
    //RestPathPattern INSTANCE_NAME_PATH              = urlPattern("/instance/{name}/");
    RestPathPattern INSTANCE_FIELD_PATH             = urlPattern("/instance/{id}/field/{id}/");
    //RestPathPattern INSTANCE_RELATION_PATH          = urlPattern("/instance/{id}/relation/");
    RestPathPattern INSTANCE_RELATION_ID_PATH       = urlPattern("/instance/{id}/relation/{id}/");
    RestPathPattern INSTANCE_RELATION_ID_ID_PATH    = urlPattern("/instance/{id}/relation/{id}/{id}/");
    RestPathPattern INSTANCE_METHOD_PATH            = urlPattern("/instance/{id}/method/{id}/");

    enum InstanceAction implements Action {
        //LIST_INSTANCES( RestServiceProtocol.Method.GET,     INSTANCES_PATH),
        //CREATE_INSTANCE(RestServiceProtocol.Method.PUT,     INSTANCE_NAME_PATH),
        GET_INSTANCE(   RestServiceProtocol.Method.GET,     INSTANCE_ID_PATH),
        DELETE_INSTANCE(RestServiceProtocol.Method.DELETE,  INSTANCE_ID_PATH),
        GET_VALUE(      RestServiceProtocol.Method.GET,     INSTANCE_FIELD_PATH),
        SET_VALUE(      RestServiceProtocol.Method.POST,    INSTANCE_FIELD_PATH),
        UPDATE_VALUE(   RestServiceProtocol.Method.POST,    INSTANCE_FIELD_PATH),
        REMOVE_VALUE(   RestServiceProtocol.Method.DELETE,  INSTANCE_FIELD_PATH),
        //GET_RELATIONS(  RestServiceProtocol.Method.GET,     INSTANCE_RELATION_PATH),
        GET_RELATION(   RestServiceProtocol.Method.GET,     INSTANCE_RELATION_ID_PATH),
        DELETE_RELATION(RestServiceProtocol.Method.DELETE,  INSTANCE_RELATION_ID_PATH),
        SET_RELATION(   RestServiceProtocol.Method.POST,    INSTANCE_RELATION_ID_ID_PATH),
        ADD_RELATION(   RestServiceProtocol.Method.PUT,     INSTANCE_RELATION_ID_ID_PATH),
        INVOKE_METHOD(  RestServiceProtocol.Method.PUT,     INSTANCE_METHOD_PATH);

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        InstanceAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
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
        @Override public Protocol.ProtocolName getProtocol() {return InstanceProtocol.INSTANCE_PROTOCOL;}
    }
}
