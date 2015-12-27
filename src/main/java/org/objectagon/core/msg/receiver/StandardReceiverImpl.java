package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class StandardReceiverImpl<A extends Address, P extends Receiver.CreateNewAddressParams, C extends StandardReceiverCtrl<P>, W extends StandardWorker> extends BasicReceiverImpl<A,P,C,W> implements StandardReceiver<A> {

    private Reactor reactor;

    public StandardReceiverImpl(C receiverCtrl) {
        super(receiverCtrl);
        reactor = receiverCtrl.getReactor();
        buildReactor(reactor.getReactorBuilder());
    }

    protected abstract void buildReactor(Reactor.ReactorBuilder reactorBuilder);

    protected Reactor.ActionInitializer getActionInitializer(W worker) {
        return worker;
    }

    protected final void handle(W worker) {
        reactor.react(worker, actionBuilder -> {
            Reactor.Action action = actionBuilder.create(getActionInitializer(worker), worker);
            if (action.initialize())
                action.run();
            worker.setHandled();
        });
    }

}
