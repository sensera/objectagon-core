package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-11-09.
 */
public abstract class StandardReceiverImpl<A extends Address, W extends StandardWorker> extends BasicReceiverImpl<A,W> implements StandardReceiver<A> {

    private Reactor reactor;

    public StandardReceiverImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<A> initializer) {
        super.initialize(serverId, timestamp, id, initializer);
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
                worker.replyWithError(e);
            }
            worker.setHandled();
        });
    }

}
