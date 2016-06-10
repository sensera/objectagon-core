package feature.util;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-04.
 */

public class InstanceManager {
    private final TestCore.TestUser developer;

    private InstanceManager(TestCore.TestUser developer) {
        this.developer = developer;
    }

    public static InstanceManager create(TestCore.TestUser developer) {
        return new InstanceManager(developer);
    }

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(TestCore.timeout);
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

    public void invokeMethod(Method.MethodIdentity methodIdentity, Instance.InstanceIdentity instanceIdentity) throws UserException {
        taskWait(
                developer.createInstanceProtocolSend(instanceIdentity).invokeMethod(methodIdentity)
        );
    }

    public void invokeMethod(Method.MethodIdentity methodIdentity, Instance.InstanceIdentity instanceIdentity, KeyValue<Method.ParamName, Message.Value> paramValue) throws UserException {
        taskWait(
                developer.createInstanceProtocolSend(instanceIdentity).invokeMethod(methodIdentity, paramValue)
        );
    }

    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }
}
