package org.objectagon.core.object.meta.data;

import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaDataChangeImpl implements Meta.ChangeMeta {
    Meta.MetaData metaData;
    Optional<Meta.MetaName> newMetaName = Optional.empty();
    private List<Consumer<List<Method.MethodIdentity>>> methodChanges = new ArrayList<>();

    public MetaDataChangeImpl(Meta.MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public Meta.ChangeMeta setName(Meta.MetaName metaName) {
        this.newMetaName = Optional.ofNullable(metaName);
        return this;
    }

    @Override
    public Meta.ChangeMeta addMethod(Method.MethodIdentity methodIdentity) {
        methodChanges.add(methodIdentities -> methodIdentities.add(methodIdentity));
        return this;
    }

    @Override
    public Meta.ChangeMeta removeMethod(Method.MethodIdentity methodIdentity) {
        methodChanges.add(methodIdentities -> methodIdentities.remove(methodIdentity));
        return this;
    }

    @Override
    public <D extends Data<Meta.MetaIdentity, StandardVersion>> D create(StandardVersion version) {
        List<Method.MethodIdentity> methods = metaData.getMethods();
        if (!methodChanges.isEmpty()) {
            List<Method.MethodIdentity> tmpMethods = new ArrayList<>(metaData.getMethods());
            methodChanges.stream().forEach(listConsumer -> listConsumer.accept(tmpMethods));
            methods = tmpMethods;
        }
        return (D) new MetaDataImpl(
                metaData.getIdentity(),
                version,
                newMetaName.orElse(metaData.getName()),
                methods
        );
    }
}
