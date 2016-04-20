package feature.util;

import lombok.Data;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-04.
 */

@Data(staticConstructor = "create")
public class InstanceManager {
    private final TestCore.TestUser developer;

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(10000000000L);
        developer.storeResponseMessage(message);
        Stream.of(aquireValues).forEach(aquireValue -> aquireValue.message(message));
        return message;
    }

    public void setValue(Instance.InstanceIdentity instanceIdentity, Field.FieldIdentity fieldIdentity, Message.Value value) throws UserException {
        taskWait(
                developer.createInstanceProtocolSend(instanceIdentity).setValue(fieldIdentity, value),
                message -> developer.setValue(FieldValue.VALUE, message.getValue(FieldValue.VALUE))
        );
    }

    public Message.Value getValue(Instance.InstanceIdentity instanceIdentity, Field.FieldIdentity fieldIdentity) throws UserException {
        Message message = taskWait(
                developer.createInstanceProtocolSend(instanceIdentity).getValue(fieldIdentity)
        );
        return message.getValue(FieldValue.VALUE).asValues().values().iterator().next();
    }

    public Instance.InstanceIdentity createInstance(InstanceClass.InstanceClassIdentity instanceClassIdentity) throws UserException {
        return taskWait(
                developer.createInstanceClassProtocolSend(instanceClassIdentity).createInstance()
        ).getValue(StandardField.ADDRESS).asAddress();
    }

    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }
}
