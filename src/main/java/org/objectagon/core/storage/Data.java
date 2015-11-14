package org.objectagon.core.storage;

/**
 * Created by christian on 2015-10-15.
 */
public interface Data<I extends Identity, V extends Version> {
    I getIdentity();
    V getVersion();

}
