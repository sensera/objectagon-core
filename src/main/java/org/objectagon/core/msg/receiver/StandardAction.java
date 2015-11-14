package org.objectagon.core.msg.receiver;

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
    public boolean initialize() {}

}
