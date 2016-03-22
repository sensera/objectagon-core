package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Name;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2016-03-06.
 */
public class NameUtil<N extends Name.Named> {
    Stream<N> names;

    public static <N extends Name.Named> NameUtil<N> create(Stream<N> names) { return new NameUtil<N>(names);}
    public static <N extends Name.Named> NameUtil<N> create(Iterable<? extends Name.Named> names) { return new NameUtil<N>(StreamSupport.stream(names.spliterator(), false).map(named -> (N) named));}

    private NameUtil(Stream<N> names) {
        this.names = names;
    }

    public Optional<N> findByName(Name name) {
        return names.filter(n -> n.getName().equals(name)).findAny();
    }


}

