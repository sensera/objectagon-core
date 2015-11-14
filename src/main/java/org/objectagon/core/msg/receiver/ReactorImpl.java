package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 2015-11-09.
 */
public class ReactorImpl<T extends Reactor.Trigger> implements Reactor<T>, Reactor.ReactorBuilder {

    private Map<Message.MessageName,Reactor.ActionBuilder> actionsByMessageName = new HashMap<>();
    private List<ActionPatternComparator> actionsByPatternComparator = new LinkedList<>();

    @Override
    public ReactorBuilder getReactorBuilder() {
        return this;
    }

    @Override
    public void react(T trigger, TriggeredActions triggeredActions) {
        Message.MessageName messageName = trigger.getMessageName();
        ActionBuilder actionBuilder = actionsByMessageName.get(messageName);
        if (actionBuilder!=null)
            triggeredActions.triggeredAction(actionBuilder);
        actionsByPatternComparator.stream().forEach(actionPatternComparator -> actionPatternComparator.react(trigger, triggeredActions)  );
    }

    @Override
    public void add(Pattern pattern, ActionBuilder actionBuilder) {
        pattern.install(new Reactor.PatternBuilder() {
            @Override
            public void setMessageNameTrigger(Message.MessageName messageName) {
                actionsByMessageName.put(messageName, actionBuilder);
            }

            @Override
            public void setPatternComparator(Reactor.PatternComparator patternComparator) {
                actionsByPatternComparator.add(new ActionPatternComparator(actionBuilder, patternComparator));
            }
        });

    }

    private class ActionPatternComparator {
        Reactor.ActionBuilder actionBuilder;
        Reactor.PatternComparator patternComparator;

        public ActionPatternComparator(Reactor.ActionBuilder action, Reactor.PatternComparator patternComparator) {
            this.actionBuilder = action;
            this.patternComparator = patternComparator;
        }

        public void react(T trigger, TriggeredActions triggeredActions) {
            if (patternComparator.compare(trigger))
                triggeredActions.triggeredAction(actionBuilder);
        }
    }
}
