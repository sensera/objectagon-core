package org.objectagon.core.storage.persistence;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNumberValue;
import org.objectagon.core.msg.message.VolatileTextValue;
import org.objectagon.core.service.AbstractProtocolTest;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.service.name.NameServiceProtocolImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.standard.StandardIdentity;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.List;

import static java.util.Arrays.asList;
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
    Message.Field personName;
    Message.Values dataValues;

    @Before
    public void setup() {
        super.setup();
        name = mock(Name.class);
        address = mock(Address.class);
        senderAddress = mock(Address.class);
        identity = StandardIdentity.standardIdentity(mock(Server.ServerId.class), 10l, 10l);
        version = StandardVersion.create(10l);
        data = mock(Data.class);
        personName = mock(Message.Field.class);
        protocol = new PersistenceServiceProtocolImpl(receiverCtrl);
        protocol.initialize(mock(Server.ServerId.class), 100, 0, null);
        Protocol.CreateSendParam createSendParam = mock(Protocol.CreateSendParam.class);

        when(createSendParam.getComposer()).thenReturn(composer);
        when(composer.getSenderAddress()).thenReturn(senderAddress);
        when(composer.alternateReceiver(any(Receiver.class))).thenReturn(composer);

        dataValues = () -> asList(VolatileTextValue.text(personName, "Pelle"));

        when(data.values()).thenReturn(asList(MessageValue.values(dataValues)));
        when(data.getIdentity()).thenReturn(identity);
        when(data.getVersion()).thenReturn(version);

        send = protocol.createSend(createSendParam);
    }

    @Test
    public void all()  {
        startTaskAndVerifySentEvelope(
                send.all(identity),
                message -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.ALL);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                });
    }

    @Test
    public void create()  {
        startTaskAndVerifySentEvelope(
                send.create(data),
                message -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.CREATE);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                    assertEquals(message.getValue(StandardField.VALUES).asValues().values(), MessageValue.values(data.values()).asValues().values());
                });
    }

    @Test
    public void get() {
        startTaskAndVerifySentEvelope(
                send.get(identity, version),
                (message) -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.GET);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                });
    }

    @Test
    public void remove() {
        startTaskAndVerifySentEvelope(
                send.remove(identity, version),
                (message) -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.REMOVE);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                });
    }

    @Test
    public void update() {
        startTaskAndVerifySentEvelope(
                send.update(data),
                (message) -> {
                    assertEquals(message.getName(), PersistenceServiceProtocol.MessageName.UPDATE);
                    assertEquals(message.getValue(StandardField.ADDRESS).asAddress(), identity);
                    assertEquals(message.getValue(Version.VERSION).asNumber(), new Long(10l));
                    assertEquals(message.getValue(StandardField.VALUES).asValues().values(), MessageValue.values(data.values()).asValues().values());
                });
    }


}