package org.objectagon.core.storage.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.TransactionManager;

/**
 * Created by christian on 2016-02-28.
 */
public class DataMessageValue extends MessageValue<Data> {

    public static DataMessageValue data(Message.Field field, Data value) { return new DataMessageValue(field, value);}
    public static DataMessageValue data(Data value) { return new DataMessageValue(Data.DATA, value);}
    public static DataMessageValue dataVersion(DataRevision value) { return new DataMessageValue(DataRevision.DATA_VERSION, value);}
    public static DataMessageValue dataTransaction(TransactionManager.TransactionData value) { return new DataMessageValue(TransactionManager.DATA_TRANSACTION, value);}

    private DataMessageValue(Message.Field field, Data value) {
        super(field, value);
    }
}
