package org.objectagon.core.object.meta.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaDataImpl extends AbstractData<Meta.MetaIdentity, StandardVersion> implements Meta.MetaData {

    public static final Meta.MetaData create(Meta.MetaIdentity identity, StandardVersion version) {
        return new MetaDataImpl(identity, version, null, Collections.EMPTY_LIST);
    }

    Meta.MetaName metaName;
    List<Method.MethodIdentity> methods;

    MetaDataImpl(Meta.MetaIdentity identity, StandardVersion version, Meta.MetaName metaName, List<Method.MethodIdentity> methods) {
        super(identity, version);
        this.metaName = metaName;
        this.methods = methods;
    }

    @Override public Meta.MetaName getName() {
        return metaName;
    }
    @Override public List<Method.MethodIdentity> getMethods() { return methods;}

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
