package org.objectagon.core.storage.persistence;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.service.AbstractProtocolTest;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
import org.objectagon.core.storage.standard.StandardIdentity;
import org.objectagon.core.storage.standard.StandardVersion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2015-11-16.
 */

public class PersistenceServiceProtocolImplTest extends AbstractProtocolTest {

    PersistenceServiceProtocolImpl protocol;
    PersistenceServiceProtocol.Send send;
    Name name;
    Address address;
    Address senderAddress;

    Identity identity;
    Version version;
    Data data;
    DataRevision dataRevision;
    Message.Field personName;

    @Before
    public void setup() {
        super.setup();
        name = mock(Name.class);
        address = mock(Address.class);
        senderAddress = mock(Address.class);
        identity = StandardIdentity.standardIdentity(mock(Server.ServerId.class), 10l, 10l);
        version = StandardVersion.create(10l);
        data = mock(Data.class);
        dataRevision = mock(DataRevision.class);
        personName = mock(Message.Field.class);
        protocol = new PersistenceServiceProtocolImpl(receiverCtrl);
        protocol.configure();
        Protocol.CreateSendParam createSendParam = mock(Protocol.CreateSendParam.class);

        when(createSendParam.getComposer()).thenReturn(composer);
        when(composer.getSenderAddress()).thenReturn(senderAddress);
        when(composer.alternateReceiver(any(Receiver.class))).thenReturn(composer);

        when(data.getIdentity()).thenReturn(identity);
        when(data.getVersion()).thenReturn(version);

        send = protocol.createSend(createSendParam);
    }

    @Test
    @Ignore
    public void all()  {
        startTaskAndVerifySentEvelope(
                send.all(identity),
                message -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.ALL_BY_ID);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                });
    }

    @Test
    @Ignore
    public void pushData()  {
        startTaskAndVerifySentEvelope(
                send.pushData(data),
                message -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.PUSH_DATA);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                    assertEquals(message.getValue(StandardField.VALUES).asValues().values(), DataMessageValue.data(data).asValues().values());
                });
    }

    @Test
    @Ignore
    public void getData() {
        startTaskAndVerifySentEvelope(
                send.getData(mock(Data.Type.class), identity, version),
                (message) -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.GET_DATA);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                });
    }

}