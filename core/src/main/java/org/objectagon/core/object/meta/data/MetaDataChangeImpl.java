package org.objectagon.core.object.meta.data;

import org.objectagon.core.object.Meta;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaDataChangeImpl implements Meta.ChangeMeta {
    Meta.MetaData metaData;
    Optional<Meta.MetaName> newMetaName = Optional.empty();

    public MetaDataChangeImpl(Meta.MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public Meta.ChangeMeta setName(Meta.MetaName metaName) {
        this.newMetaName = Optional.ofNullable(metaName);
        return this;
    }

    @Override
    public <D extends Data<Meta.MetaIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new MetaDataImpl(
                metaData.getIdentity(),
                version,
                newMetaName.orElse(metaData.getName()));
    }
}
