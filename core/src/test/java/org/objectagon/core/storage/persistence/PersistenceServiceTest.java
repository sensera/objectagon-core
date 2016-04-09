package org.objectagon.core.storage.persistence;

import org.junit.Before;
import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.AbstractProtocolTest;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.standard.StandardIdentity;
import org.objectagon.core.storage.standard.StandardVersion;

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
        identity = StandardIdentity.standardIdentity(mock(Server.ServerId.class), 10l, 10l);
        version = StandardVersion.create(10l);
        data = mock(Data.class);
        personName = mock(Message.Field.class);
        persistenceServiceProtocol = mock(PersistenceServiceProtocol.class);
        persistenceService = new PersistenceService(receiverCtrl);
        persistenceService.configure();
        standardProtocol = mock(StandardProtocol.class);
        standardReply = mock(StandardProtocol.StandardReply.class);

        when(receiverCtrl.createReceiver(eq(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL), isNull(Receiver.Configurations.class))).thenReturn(persistenceServiceProtocol);
        when(receiverCtrl.createReceiver(eq(StandardProtocol.STANDARD_PROTOCOL), isNull(Receiver.Configurations.class))).thenReturn(standardProtocol);
        when(standardProtocol.createReply(any(Protocol.CreateReplyParam.class))).thenReturn(standardReply);

       // when(data.values()).thenReturn(asList(VolatileTextValue.text(personName, "Pelle")));
    }

    /*@Test
    public void create() {
        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.PUSH)
                .setValue(version.asValue())
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, identity))
                .setValue(MessageValue.values(data.values()));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

        assertEquals(1, persistenceService.stored.size());
        assertEquals(data.values(), persistenceService.stored.get(new PersistenceService.Key(identity, version)).values());
    }

    @Test
    public void update() {
        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.UPDATE)
                .setValue(version.asValue())
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, identity))
                .setValue(MessageValue.values(data.values()));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

        assertEquals(1, persistenceService.stored.size());
        assertEquals(data.values(), persistenceService.stored.get(new PersistenceService.Key(identity, version)).values());
    }

    @Test
    public void get() {
        persistenceService.stored.put(new PersistenceService.Key(identity, version), data::values);

        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.GET_DATA)
                .setValue(version.asValue())
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, identity));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

    }

    @Test
    public void remove() {
        persistenceService.stored.put(new PersistenceService.Key(identity, version), data::values);

        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.REMOVE)
                .setValue(version.asValue())
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, identity));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

        assertEquals(0, persistenceService.stored.size());
    }

    @Test
    public void all() {
        persistenceService.stored.put(new PersistenceService.Key(identity, version), data::values);

        SimpleMessage message = SimpleMessage.simple(PersistenceServiceProtocol.MessageName.ALL_BY_ID)
                .setValue(VolatileAddressValue.address(StandardField.ADDRESS, identity));
        StandardEnvelope envelope = new StandardEnvelope(sender, target, message);
        persistenceService.receive(envelope);

    } */
}

