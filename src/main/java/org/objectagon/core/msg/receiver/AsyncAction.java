package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class AsyncAction<I extends Reactor.ActionInitializer, C extends Reactor.ActionContext> implements Reactor.Action {

    protected I initializer;
    protected C context;
    protected Task.SuccessAction successAction = (messageName, values) -> context.replyWithParam(values.iterator().next());
    protected Task.FailedAction failedAction = (errorClass, errorKind, values) -> context.replyWithError(new SevereError(errorClass, errorKind, values));

    public AsyncAction(I initializer, C context) {
        this.initializer = initializer;
        this.context = context;
    }

    @Override
    public boolean initialize() throws UserException { return true; }

    public void setSuccessAction(Task.SuccessAction successAction) {
        this.successAction = successAction;
    }

    public void setFailedAction(Task.FailedAction failedAction) {
        this.failedAction = failedAction;
    }

    @Override
    public final void run() {
        try {
            internalRun(context)
                    .addSuccessAction(successAction)
                    .addFailedAction(failedAction)
                    .start();
        } catch (UserException e) {
            context.replyWithError(e);
        }
    }

    protected abstract Task internalRun(C actionContext) throws UserException;


}
