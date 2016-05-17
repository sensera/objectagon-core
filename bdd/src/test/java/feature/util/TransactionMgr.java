package feature.util;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-20.
 */
public class TransactionMgr {
    private final TestCore.TestUser developer;

    private TransactionMgr(TestCore.TestUser developer) {
        this.developer = developer;
    }

    public static TransactionMgr create(TestCore.TestUser developer) {
        return new TransactionMgr(developer);
    }

    private Message taskWait(Task task, AquireValue... aquireValues) throws UserException {
        Message message = TaskWait.create(task).startAndWait(TestCore.timeout);
        developer.storeResponseMessage(message);
        Stream.of(aquireValues).forEach(aquireValue -> aquireValue.message(message));
        return message;
    }

    @FunctionalInterface
    interface AquireValue {
        void  message(Message message);
    }

}
