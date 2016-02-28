package org.objectagon.core.storage;

import org.objectagon.core.msg.Converter;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-15.
 */
public interface Data<I extends Identity, V extends Version>  {
    Message.Field DATA = NamedField.values("Data");

    I getIdentity();
    V getVersion();

    void convert(Converter.FromData<Data<I,V>> fromData);
}
