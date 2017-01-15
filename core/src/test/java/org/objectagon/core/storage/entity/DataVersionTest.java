package org.objectagon.core.storage.entity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.storage.*;
import org.objectagon.core.testutil.ReceiveConsumer;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by christian on 2016-03-29.
 */
public class DataVersionTest {
    Identity identity;
    Version version;
    Version nextVersion = mock(Version.class);
    DataVersion dataVersion;

    @Before
    public void setup() {
        identity = mock(Identity.class);
        version = mock(Version.class);
        dataVersion = DataVersionImpl.create(identity, version, 0L, (counter) -> nextVersion);
    }

    @Test
    @Ignore
    public void testCreate() {
        assertThat(dataVersion.rootNode().isPresent(), is(equalTo(false)));
    }

    @Test
    @Ignore
    public void testAddTransaction() throws UserException {
        DataVersion.ChangeDataVersion change = (DataVersion.ChangeDataVersion) dataVersion.change();

        Transaction transaction = mock(Transaction.class);
        ReceiveConsumer<Version> version1 = new ReceiveConsumer<>();
        change.add(transaction, version1, Data.MergeStrategy.Uppgrade);

        Version version2 = mock(Version.class);
        DataVersion newDataVersion = (DataVersion) change.create(version2);

        assertThat(dataVersion.rootNode().isPresent(), is(equalTo(false)));
        Optional<DataVersion.TransactionVersionNode> transactionVersionNodeOptional = newDataVersion.rootNode();
        assertThat(transactionVersionNodeOptional.isPresent(), is(equalTo(true)));

        DataVersion.TransactionVersionNode transactionVersionNode = transactionVersionNodeOptional.get();
        assertThat(transactionVersionNode.getMergeStrategy(),is(equalTo(Data.MergeStrategy.Uppgrade)));
        assertThat(transactionVersionNode.getNextVersion().isPresent(), is(equalTo(false)));
        assertThat(transactionVersionNode.getTransaction(), is(not(nullValue())));
        assertThat(transactionVersionNode.getTransaction().equals(transaction),is(equalTo(true)));
        assertThat(transactionVersionNode.getVersion(),is(equalTo(version1.getV())));
    }


    @Test
    @Ignore
    public void testNewVersion() throws UserException {
        DataVersion.ChangeDataVersion change = (DataVersion.ChangeDataVersion) dataVersion.change();

        Transaction transaction = mock(Transaction.class);
        ReceiveConsumer<Version> version1 = new ReceiveConsumer<>();
        change.add(transaction, version1, Data.MergeStrategy.Uppgrade);

        Version version2 = mock(Version.class);
        DataVersion newDataVersion = (DataVersion) change.create(version2);

        DataVersion.ChangeDataVersion change2 = (DataVersion.ChangeDataVersion) newDataVersion.change();

        change2.newVersion(transaction, (ver) ->  assertThat(ver,is(equalTo(nextVersion))));

        DataVersion newDataVersion2 = (DataVersion) change2.create(mock(Version.class));

        Optional<DataVersion.TransactionVersionNode> transactionVersionNodeOptional = newDataVersion2.rootNode();
        DataVersion.TransactionVersionNode transactionVersionNode = transactionVersionNodeOptional.get();

        assertThat(transactionVersionNode.getVersion(),is(equalTo(nextVersion)));
    }

}
