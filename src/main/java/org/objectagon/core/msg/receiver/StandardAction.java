package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;

import java.util.Optional;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class StandardAction <I extends Reactor.ActionInitializer, C extends Reactor.ActionContext> implements Reactor.Action {

    protected I initializer;
    protected C context;

    public StandardAction(I initializer, C context) {
        this.initializer = initializer;
        this.context = context;
    }

    @Override
    public boolean initialize() throws UserException { return true; }

    @Override
    public final void run() {
        try {
            Optional<Message.Value> value = internalRun();
            if (value.isPresent())
                context.replyWithParam(value.get());
            else
                context.replyOk();
        } catch (UserException e) {
            context.replyWithError(e);
        }
    }

    protected abstract Optional<Message.Value> internalRun() throws UserException;
}
