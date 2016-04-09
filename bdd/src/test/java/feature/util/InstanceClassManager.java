package feature.util;

import lombok.Data;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-04.
 */

@Data(staticConstructor = "create")
public class InstanceClassManager {
    private final TestCore.TestUser developer;

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(1000L);
        developer.storeResponseMessage(message);
        Stream.of(aquireValues).forEach(aquireValue -> aquireValue.message(message));
        return message;
    }

    public InstanceClass.InstanceClassIdentity createInstanceClass() throws UserException {
        return taskWait(
                developer.createInstanceClassEntityServiceProtocol().create(),
                message -> developer.setValue(InstanceClass.INSTANCE_CLASS_IDENTITY, message.getValue(StandardField.ADDRESS))
        ).getValue(StandardField.ADDRESS).asAddress();
    }

    public void setInstanceClassName(InstanceClass.InstanceClassIdentity instanceClassIdentity, String typeName) throws UserException {
        taskWait(
                developer.createInstanceClassProtocolSend(instanceClassIdentity).setName(InstanceClassNameImpl.create(typeName)),
                message -> developer.setValue(InstanceClass.INSTANCE_CLASS_IDENTITY, message.getValue(StandardField.ADDRESS))
        );
    }

    public InstanceClass.InstanceClassIdentity findInstanceClassWithName(String typeName) throws UserException {
        Message msg = taskWait(
                developer.createInstanceClassEntityServiceProtocol().find(InstanceClassNameImpl.create(typeName)),
                message -> developer.setValue(InstanceClass.INSTANCE_CLASS_IDENTITY, message.getValue(StandardField.ADDRESS))
        );
        return MessageValueFieldUtil.create(msg.getValues()).getValueByField(Identity.IDENTITY).asAddress();
    }

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return developer.getValue(InstanceClass.INSTANCE_CLASS_IDENTITY).get().asAddress();
    }

    public void addFieldToInstanceClass(InstanceClass.InstanceClassIdentity instanceClassIdentity, String fieldName) throws UserException {
        taskWait(
                developer.createInstanceClassProtocolSend(instanceClassIdentity).addField(FieldNameImpl.create(fieldName)),
                message -> developer.setValue(Field.FIELD_IDENTITY, message.getValue(StandardField.ADDRESS))
        );
    }

    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }
}
