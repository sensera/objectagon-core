package org.objectagon.core.storage.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.Data;

/**
 * Created by christian on 2016-02-28.
 */
public class DataMessageValue extends MessageValue<Data> {

    public static DataMessageValue data(Message.Field field, Data value) { return new DataMessageValue(field, value);}
    public static DataMessageValue data(Data value) { return new DataMessageValue(Data.DATA, value);}

    private DataMessageValue(Message.Field field, Data value) {
        super(field, value);
    }
}
