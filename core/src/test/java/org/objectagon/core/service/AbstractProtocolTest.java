package org.objectagon.core.service;

import org.mockito.ArgumentCaptor;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.task.Task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class AbstractProtocolTest {

    protected Receiver.ReceiverCtrl receiverCtrl;
    protected Composer composer;
    protected Envelope envelope;

    public void setup() {
        receiverCtrl = mock(Receiver.ReceiverCtrl.class);
        composer = mock(Composer.class);
        envelope = mock(Envelope.class);
    }

    protected void startTaskAndVerifySentEvelope(Task task, AssertMessage assertMessage) {
        task.start();
        verify(receiverCtrl).transport(any(Envelope.class));
        ArgumentCaptor<Message> messageCapture = ArgumentCaptor.forClass(Message.class);
        verify(composer).create(messageCapture.capture());
        assertMessage.assertMessage(messageCapture.getValue());
    }

    @FunctionalInterface
    public interface AssertMessage {
        void assertMessage(Message message);
    }
}
