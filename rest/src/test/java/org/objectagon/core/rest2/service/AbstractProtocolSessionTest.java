package org.objectagon.core.rest2.service;

import org.mockito.ArgumentCaptor;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.task.Task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2015-11-16.
 */
public abstract class AbstractProtocolSessionTest<P extends Protocol.Session> {

    protected Receiver.ReceiverCtrl receiverCtrl;
    protected Composer composer;
    protected Envelope envelope;
    protected Address senderAddress;
    protected Protocol.CreateSendParam createSendParam;
    protected Receiver.AddressConfigurationParameters addressConfigurationParameters;
    protected P send;

    public void setup() {
        receiverCtrl = mock(Receiver.ReceiverCtrl.class);
        composer = mock(Composer.class);
        envelope = mock(Envelope.class);
        senderAddress = mock(Address.class);

        createSendParam = mock(Protocol.CreateSendParam.class);

        when(createSendParam.getComposer()).thenReturn(composer);
        when(composer.getSenderAddress()).thenReturn(senderAddress);
        when(composer.alternateReceiver(any(Receiver.class))).thenReturn(composer);

        addressConfigurationParameters = new Receiver.AddressConfigurationParameters() {
            @Override public Server.ServerId getServerId() {return LocalServerId.local("local");}
            @Override public Long getId() {return 8798723L;}
            @Override public Long getTimeStamp() {return 29879834L;}
        };

        send = createProtocolSend(receiverCtrl, createSendParam);
    }

    public abstract P createProtocolSend(Receiver.ReceiverCtrl receiverCtrl, Protocol.CreateSendParam createSendParam);

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
