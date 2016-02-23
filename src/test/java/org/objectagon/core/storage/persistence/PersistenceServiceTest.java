package org.objectagon.core.storage.persistence;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.*;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.AbstractProtocolTest;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 2016-02-16.
 */
public class PersistenceServiceTest extends AbstractProtocolTest {

    private final String testName = "TestName";

    PersistenceService persistenceService;
    Address sender;
    Address target;
    Name name;
    PersistenceServiceProtocol persistenceServiceProtocol;
    StandardProtocol standardProtocol;
    StandardProtocol.StandardReply standardReply;

    Identity identity;
    Version version;
    Data data;
    Message.Field personName;

    @Before
    public void setup() {
        super.setup();
        sender = mock(Address.class);
        target = mock(Address.class);
        name = StandardName.name(testName);
        identity = mock(Identity.class);
        version = mock(Version.class);
        data = mock(Data.class);
        personName = mock(Message.Field.class);
        persistenceServiceProtocol = mock(PersistenceServiceProtocol.class);
        persistenceService = new PersistenceService(receiverCtrl);
        persistenceService.initialize(mock(Server.ServerId.class), 100, 0, null);
        standardProtocol = mock(StandardProtocol.class);
        standardReply = mock(StandardProtocol.StandardReply.class);

        when(receiverCtrl.createReceiver(eq(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL), isNull(Receiver.Initializer.class))).thenReturn(persistenceServiceProtocol);
        when(receiverCtrl.createReceiver(eq(StandardProtocol.STANDARD_PROTOCOL), isNull(Receiver.Initializer.class))).thenReturn(standardProtocol);
        when(standardProtocol.createReply(any(Protocol.CreateReplyParam.class))).thenReturn(standardReply);

        when(version.asValue()).thenReturn(VolatileNumberValue.number(Version.VERSION, 10l));

        when(data.values()).thenReturn(asList(VolatileTextValue.text(personName, "Pelle")));
    }

    @Test
    public void create() {
        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.CREATE)
                .setValue(version.asValue())
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, sender))
                .setValue(VolatileTextValue.text(personName, "Pelle"));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

        assertEquals(data.values(), persistenceService.stored.get(new PersistenceService.Key(identity, version)));
    }

/*    @Test
    public void all() {
        persistenceService.addressByName.put(testName, sender);

        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);
    }

    @Test
    public void unregisterName() {
        persistenceService.addressByName.put(testName, sender);

        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.UNREGISTER_NAME)
                .setValue(VolatileNameValue.name(StandardField.NAME, name));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

        assertNull(persistenceService.addressByName.get(testName));
    }*/
}

