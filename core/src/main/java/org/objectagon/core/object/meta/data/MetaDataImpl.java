package org.objectagon.core.object.meta.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Meta;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaDataImpl extends AbstractData<Meta.MetaIdentity, StandardVersion> implements Meta.MetaData {

    public static final Meta.MetaData create(Meta.MetaIdentity identity, StandardVersion version) {
        return new MetaDataImpl(identity, version, null);
    }

    Meta.MetaName metaName;

    MetaDataImpl(Meta.MetaIdentity identity, StandardVersion version, Meta.MetaName metaName) {
        super(identity, version);
    }

    @Override
    public Meta.MetaName getName() {
        return metaName;
    }

    @Override
    public <C extends Change<Meta.MetaIdentity, StandardVersion>> C change() {
        return (C) new MetaDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        return Arrays.asList(MessageValue.name(Meta.META_NAME, metaName));
    }

    @Override public Type getDataType() {return Meta.DATA_TYPE;}
}
