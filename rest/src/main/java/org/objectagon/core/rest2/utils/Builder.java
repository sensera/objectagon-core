package org.objectagon.core.rest2.utils;

/**
 * Created by christian on 2017-09-12.
 */
public interface Builder<N,V> {

    ListBuilder<N,V> createListBuilder();
    MapBuilder<N,V> createMapBuilder();

    interface ListBuilder<N,V> extends CreateMapBuilder<N,V> {
    }

    interface MapBuilder<N,V> extends CreateMapBuilder<N,V>, CreateListBuilder<N,V> {
        MapBuilder<N,V> add(N name, V value);
    }

    interface CreateListBuilder<N,V> {
        ListBuilder<N,V> createListBuilder();
    }

    interface CreateMapBuilder<N,V> {
        MapBuilder<N,V> createMapBuilder();
    }

}
