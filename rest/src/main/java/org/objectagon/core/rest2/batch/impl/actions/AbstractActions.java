package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.Actions;
import org.objectagon.core.rest2.batch.impl.DataPortalTargetProtocolActionImpl;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;
import org.objectagon.core.rest2.batch.impl.SimpleTargetProtocolActionImpl;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.utils.NameValue;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by christian on 2017-04-17.
 */
public abstract class AbstractActions<S extends Protocol.Send> implements Actions {

    public static Name RESOLVE_INSTANCE_CLASS_ID    = StandardName.name("RESOLVE_INSTANCE_CLASS_ID");
    public static Name RESOLVE_META_CLASS_ID        = StandardName.name("RESOLVE_META_CLASS_ID");
    public static Name RESOLVE_FIELD_ID             = StandardName.name("RESOLVE_FIELD_ID");
    public static Name RESOLVE_RELATION_CLASS_ID    = StandardName.name("RESOLVE_RELATION_CLASS_ID");
    public static Name RESOLVE_INSTANCE_ID          = StandardName.name("RESOLVE_INSTANCE_ID");
    public static Name RESOLVE_RELATION_ID          = StandardName.name("RESOLVE_RELATION_ID");
    public static Name RESOLVE_METHOD_ID            = StandardName.name("RESOLVE_METHOD_ID");

    Protocol.ProtocolName protocolName;

    public AbstractActions(Protocol.ProtocolName protocolName) {
        this.protocolName = protocolName;
    }

    @Override
    public final <A extends BatchUpdate.Action> Optional<A> findActionByKind(LocalActionKind actionKind) {
        return Optional.ofNullable(internalFindActionByKind(actionKind));
    }

    protected abstract <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind);

    <E extends SimpleTargetProtocolActionImpl> E createAction(ProtocolTask.SendMessageAction<S> action) {
        return (E) new SimpleTargetProtocolActionImpl<S>(protocolName, action);
    }

    <D extends Actions.DataPortal, E extends DataPortalTargetProtocolActionImpl> E
    createDataAction(Actions.DataPortalSend<S, D> action, D dataPortal) {
        return (E) new DataPortalTargetProtocolActionImpl<>(protocolName, action);
    }

    public static Function<BatchUpdate.ActionContext, Optional<Address>> createIdentityContextFinder(Name resolveName) {
        return actionContext -> actionContext.getValue(resolveName).map(Message.Value::asAddress);
    }

    public static Function<Iterable<Message.Value>, Optional<NameValue>> createIdentifierResolver(Name resolveName) {
        return values -> Optional.of(NameValue.create(resolveName, MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS)));
    }


}
