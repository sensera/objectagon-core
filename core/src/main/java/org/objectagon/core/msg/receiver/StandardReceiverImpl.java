package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class StandardReceiverImpl<A extends Address, W extends StandardWorker> extends BasicReceiverImpl<A,W> implements StandardReceiver<A> {

    private Reactor reactor;

    public StandardReceiverImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        reactor = new ReactorImpl<>();
        buildReactor(reactor.getReactorBuilder());
    }

    protected abstract void buildReactor(Reactor.ReactorBuilder reactorBuilder);

    protected Reactor.ActionInitializer getActionInitializer(W worker) {
        return worker;
    }

    protected final void handle(W worker) {
        reactor.react(worker, actionBuilder -> {
            Reactor.Action action = actionBuilder.create(getActionInitializer(worker), worker);
            try {
                if (action.initialize())
                    action.run();
            } catch (UserException e) {
                e.printStackTrace();
                worker.replyWithError(e);
            }
            worker.setHandled();
        });
    }

}
