package org.objectagon.core.utils;

/**
 * Created by christian on 2016-05-29.
 */
public interface KeyValue<Key,Value> {
    Key getKey();
    Value getValue();
}
