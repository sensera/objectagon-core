package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-15.
 */
public interface Data<I extends Identity, V extends Version> extends Message.Values {

    interface Type extends Name {}

    enum MergeStrategy {
        None,
        OverWrite,
        Uppgrade, // Will merge old and new data
        TextMerge
    }

    Message.Field DATA = NamedField.values("Data");
    I getIdentity();

    V getVersion();

    Type getDataType();

    <C extends Change<I, V>> C change();

    interface Change<I extends Identity, V extends Version> {
        <D extends Data<I,V>> D create(V version);
    }

}
