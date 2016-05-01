package org.objectagon.core.object.relationclass;

import lombok.Data;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;

import java.util.function.Supplier;

/**
 * Created by christian on 2016-05-01.
 */
@Data(staticConstructor = "create")
public class RelationDirectionUtil {

    public final RelationClass.RelationDirection relationDirection;

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity(
            Supplier<InstanceClass.InstanceClassIdentity> instanceClassIdentityFrom,
            Supplier<InstanceClass.InstanceClassIdentity> instanceClassIdentityTo) {
        switch (relationDirection) {
            case RELATION_FROM: return instanceClassIdentityFrom.get();
            case RELATION_TO: return instanceClassIdentityTo.get();
            default: throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
        }
    }

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity(MessageValueFieldUtil messageValueFieldUtil) {
        return messageValueFieldUtil.getValueByFieldOption(getField())
                .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(InstanceClass.INSTANCE_CLASS_IDENTITY,"UNKNOWN")))
                .asAddress();
    }

    public Message.Field getField() {
        switch (relationDirection) {
            case RELATION_FROM: return RelationClass.INSTANCE_CLASS_FROM;
            case RELATION_TO: return RelationClass.INSTANCE_CLASS_TO;
            default: throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
        }
    }

    public Instance.InstanceIdentity getInstance(Supplier<Instance.InstanceIdentity> instanceIdentityFrom,
                                                 Supplier<Instance.InstanceIdentity> instanceIdentityTo) {
        switch (relationDirection) {
            case RELATION_FROM: return instanceIdentityFrom.get();
            case RELATION_TO: return instanceIdentityTo.get();
            default: throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
        }
    }


    public Instance.InstanceIdentity getInstanceIdentity(MessageValueFieldUtil messageValueFieldUtil) {
        return messageValueFieldUtil.getValueByFieldOption(getField())
                .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(InstanceClass.INSTANCE_CLASS_IDENTITY,"UNKNOWN")))
                .asAddress();
    }
}
