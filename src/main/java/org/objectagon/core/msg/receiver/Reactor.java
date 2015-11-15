package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-09.
 */
public interface Reactor<T extends Reactor.Trigger> {

    ReactorBuilder getReactorBuilder();

    void react(T trigger, TriggeredActions triggeredActions);

    interface Trigger {
        Message.MessageName getMessageName();
    }

    interface ReactorBuilder {
        <I extends ActionInitializer, C extends ActionContext> void add(Pattern pattern, ActionBuilder<I,C> action);
    }

    interface Pattern {
        void install(PatternBuilder patternBuilder);
    }

    interface ActionBuilder<I extends ActionInitializer, C extends ActionContext> {
         Action create(I initializer, C context);
    }

    interface Action {
        boolean initialize();
        void run();
    }

    interface ActionInitializer {}

    interface ActionContext {
        void replyOk();
        void replyWithParam(Message.Value value);
        void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile);
    }

    interface PatternBuilder {
        void setMessageNameTrigger(Message.MessageName messageName);
        void setPatternComparator(PatternComparator patternComparator);
    }

    interface PatternComparator<T extends Reactor.Trigger> {
        boolean compare(T trigger);
    }

    interface TriggeredActions {
        void triggeredAction(ActionBuilder action);
    }
}
