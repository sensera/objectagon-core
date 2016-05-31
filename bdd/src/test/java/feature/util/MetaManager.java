package feature.util;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-04.
 */

public class MetaManager {
    private final TestCore.TestUser developer;

    private MetaManager(TestCore.TestUser developer) {
        this.developer = developer;
    }

    public static MetaManager create(TestCore.TestUser developer) {
        return new MetaManager(developer);
    }

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(TestCore.timeout);
        developer.storeResponseMessage(message);
        Stream.of(aquireValues).forEach(aquireValue -> aquireValue.message(message));
        return message;
    }

    public Method.MethodIdentity createMethodInMeta(Meta.MetaIdentity metaIdentity) throws UserException {
        return taskWait(
                developer.createMetaProtocolSend(metaIdentity).createMethod(),
                message -> developer.setValue(Method.METHOD_IDENTITY, message.getValue(StandardField.ADDRESS))
        ).getValue(StandardField.ADDRESS).asAddress();
    }

    public Meta.MetaIdentity createMeta() throws UserException {
        return taskWait(
                developer.createMetaEntityServiceProtocol().create(),
                message -> developer.setValue(Meta.META_IDENTITY, message.getValue(StandardField.ADDRESS))
        ).getValue(StandardField.ADDRESS).asAddress();
    }

    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }
}
