package org.objectagon.core.object.method;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.Method;
import org.objectagon.core.storage.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.LazyInitializedConfigurations;

public class MethodImplTest {

    MethodImpl method;
    @Mock Receiver.ReceiverCtrl receiverCtrl;
    @Mock MethodImpl.MethodWorker methodWorker;
    @Mock
    DataRevision<Method.MethodIdentity,Version> dataRevision;
    @Mock Transaction transaction;
    @Mock Version version;
    @Mock DataRevision.TransactionVersionNode transactionVersionNode;
    @Mock PersistenceServiceProtocol.Send persistenceServiceProtocolSend;
    @Mock Task task;

    @Before public void setup() {
        MockitoAnnotations.initMocks(this);

        method = new MethodImpl(receiverCtrl);

        LazyInitializedConfigurations configurations = LazyInitializedConfigurations.create();

        configurations.add(Method.ENTITY_CONFIG_NAME, () -> new Method.EntityConfig() {
            @Override public <I extends Identity, V extends Version> DataRevision<I, V> getDataVersion(I identity) {
                return (DataRevision<I, V>) dataRevision;
            }
            @Override public long getDataVersionCounter() {return 0;}
            @Override public Message.Values initialParams() {return null;}
        });
        configurations.add(Receiver.ADDRESS_CONFIGURATIONS, () -> new Receiver.AddressConfigurationParameters() {
            @Override public Server.ServerId getServerId() {return null;}
            @Override public Long getId() {return 0L;}
            @Override public Long getTimeStamp() {return 0L;}
        });

        method.configure(configurations);
    }

    @Ignore
    @Test public void test() {
        //Given
        //method.

        //When
        //method.handle(methodWorker);

    }


}