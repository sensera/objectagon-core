package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.UserException;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class ForwardAction<I extends Reactor.ActionInitializer, C extends Reactor.ActionContext> implements Reactor.Action {

    protected I initializer;
    protected C context;

    public ForwardAction(I initializer, C context) {
        this.initializer = initializer;
        this.context = context;
    }

    @Override
    public boolean initialize() throws UserException { return true; }

    @Override
    public final void run() {
        try {
            internalRun();
        } catch (UserException e) {
            context.replyWithError(e);
        }
    }

    protected abstract void internalRun() throws UserException;
}
