package org.objectagon.core.service;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class AbstractProtocolTest {

    protected Receiver.AddressConfigurationParameters addressConfigurationParameters;
    protected Receiver.ReceiverCtrl receiverCtrl;
    protected Composer composer;
    protected Envelope envelope;

    public void setup() {
        receiverCtrl = Mockito.mock(Receiver.ReceiverCtrl.class);
        composer = Mockito.mock(Composer.class);
        envelope = Mockito.mock(Envelope.class);

        addressConfigurationParameters = new Receiver.AddressConfigurationParameters() {
            @Override public Server.ServerId getServerId() {return LocalServerId.local("local");}
            @Override public Long getId() {return 8798723L;}
            @Override public Long getTimeStamp() {return 29879834L;}
        };
    }

    protected void startTaskAndVerifySentEvelope(Task task, AssertMessage assertMessage) {
        task.start();
        Mockito.verify(receiverCtrl).transport(Matchers.any(Envelope.class));
        ArgumentCaptor<Message> messageCapture = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(composer).create(messageCapture.capture());
        assertMessage.assertMessage(messageCapture.getValue());
    }

    @FunctionalInterface
    public interface AssertMessage {
        void assertMessage(Message message);
    }
}
