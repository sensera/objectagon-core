package feature.util;

import lombok.Data;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-04.
 */

@Data(staticConstructor = "create")
public class RelationManager {
    private final TestCore.TestUser developer;

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(TestCore.timeout);
        developer.storeResponseMessage(message);
        Stream.of(aquireValues).forEach(aquireValue -> aquireValue.message(message));
        return message;
    }

    public Relation.RelationIdentity addRelation(Instance.InstanceIdentity instanceIdentityFrom,
                                                           Instance.InstanceIdentity instanceIdentityTo,
                                                           RelationClass.RelationClassIdentity relationClassIdentity) throws UserException {
        return taskWait(
                developer.createInstanceProtocolSend(instanceIdentityFrom).addRelation(relationClassIdentity, instanceIdentityTo),
                message -> developer.setValue(Relation.RELATION_IDENTITY, message.getValue(StandardField.ADDRESS))
        ).getValue(StandardField.ADDRESS).asAddress();
    }

    public void removeRelation(Instance.InstanceIdentity instanceToRemove, Instance.InstanceIdentity targetInstance, RelationClass.RelationClassIdentity relationClassIdentity) throws UserException {
        taskWait(
                developer.createInstanceProtocolSend(targetInstance).removeRelation(relationClassIdentity, instanceToRemove),
                message -> developer.setValue(Relation.RELATION_IDENTITY, message.getValue(StandardField.ADDRESS))
        );
    }


    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }
}
