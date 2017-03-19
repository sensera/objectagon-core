package org.objectagon.core.rest2.service;

import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.OneReceiverConfigurations;

import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2017-03-05.
 */
public abstract class AbstractServiceTest<S extends AbstractService> {

    protected Receiver.ReceiverCtrl receiverCtrl;
    protected Composer composer;
    protected Envelope envelope;
    protected Address sender;
    protected Address target;
    protected Receiver.AddressConfigurationParameters addressConfigurationParameters;
    protected S targetService;
    NameServiceProtocol nameServiceProtocol;
    StandardProtocol standardProtocol;
    StandardProtocol.StandardReply standardReply;
    TaskBuilder taskBuilder;

    @Before
    public void setup() {
        receiverCtrl = Mockito.mock(Receiver.ReceiverCtrl.class);
        composer = Mockito.mock(Composer.class);
        envelope = Mockito.mock(Envelope.class);
        sender = Mockito.mock(Address.class);
        target = Mockito.mock(Address.class);
        addressConfigurationParameters = new Receiver.AddressConfigurationParameters() {
            @Override public Server.ServerId getServerId() {return LocalServerId.local("local");}
            @Override public Long getId() {return 8798723L;}
            @Override public Long getTimeStamp() {return 29879834L;}
        };

        nameServiceProtocol = Mockito.mock(NameServiceProtocol.class);
        standardProtocol = Mockito.mock(StandardProtocol.class);
        standardReply = Mockito.mock(StandardProtocol.StandardReply.class);
        taskBuilder = mock(TaskBuilder.class);

        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(NameServiceProtocol.NAME_SERVICE_PROTOCOL), Matchers.isNull(Receiver.Configurations.class))).thenReturn(nameServiceProtocol);
        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(StandardProtocol.STANDARD_PROTOCOL), Matchers.isNull(Receiver.Configurations.class))).thenReturn(standardProtocol);
        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(StandardProtocol.STANDARD_PROTOCOL), Matchers.any(Receiver.Configurations.class))).thenReturn(standardProtocol);
        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(StandardProtocol.STANDARD_PROTOCOL), Matchers.any(Receiver.Configurations.class), Matchers.any(Receiver.Configurations.class))).thenReturn(standardProtocol);
        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(StandardProtocol.STANDARD_PROTOCOL))).thenReturn(standardProtocol);
        Mockito.when(receiverCtrl.createReceiver(Matchers.eq(StandardTaskBuilder.STANDARD_TASK_BUILDER))).thenReturn(taskBuilder);
        Mockito.when(standardProtocol.createReply(Matchers.any(Protocol.CreateReplyParam.class))).thenReturn(standardReply);

        targetService = createTargetService();

        configureTargetService();
    }

    protected void configureTargetService() {
        targetService.configure(getAddressReceiverConfigurations());
    }

    protected OneReceiverConfigurations getAddressReceiverConfigurations() {
        return OneReceiverConfigurations.create(Receiver.ADDRESS_CONFIGURATIONS, addressConfigurationParameters);
    }

    protected abstract S createTargetService();


    protected StandardEnvelope createStandardEnvelope(SimpleMessage message) {
        return new StandardEnvelope(sender, target, message, Mockito.mock(Message.Values.class));
    }

}
