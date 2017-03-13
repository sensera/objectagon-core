package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathValue;
import org.objectagon.core.storage.Identity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class RestPathImpl implements RestServiceActionLocator.RestPath {

    public static RestServiceActionLocator.RestPath create(Name path, RestServiceActionLocator.IdentityLookup identityLookup) {
        Indexer indexer = new Indexer(identityLookup);
        if (path == null || path.toString().equals("/") || path.toString().equals(""))
            return new RestPathImpl(Collections.EMPTY_LIST);
        return new RestPathImpl(Stream.of(path.toString().split("/"))
                .filter(s -> !s.isEmpty())
                .map(indexer::createRestPathValue)
                .collect(Collectors.toList()));
    }

    private List<RestPathValue> pathValues = new ArrayList<>();

    public RestPathImpl(List<RestPathValue> pathValues) {
        this.pathValues = pathValues;
    }

    public Stream<RestPathValue> values() { return pathValues.stream(); }

    @Override
    public Message.Value asValue() {
        return MessageValue.text(pathValues.stream().map(Object::toString).collect(Collectors.joining("/")));
    }

    @Override
    public Message.Value valueAtIndex(int index) {
        return MessageValue.text(pathValues.get(index).toString());
    }

    @Override
    public int size() {
        return pathValues.size();
    }

    private static class RestPathValueImpl implements RestPathValue {
        private int index;
        private String value;
        RestServiceActionLocator.IdentityLookup identityLookup;

        public RestPathValueImpl(int index, String value, RestServiceActionLocator.IdentityLookup identityLookup) {
            this.index = index;
            this.value = value;
            this.identityLookup = identityLookup;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public RestServiceActionLocator.RestMatchRating isText(RestPathPatternImpl.MatchPatternDetails matchPatternDetails) {
            if (!matchPatternDetails.isText())
                return RestServiceActionLocator.RestMatchRating.None;
            if (matchPatternDetails.getText().equalsIgnoreCase(value))
                return RestServiceActionLocator.RestMatchRating.Perfect;
            return RestServiceActionLocator.RestMatchRating.None;
        }

        @Override
        public RestServiceActionLocator.RestMatchRating isId(RestPathPatternImpl.MatchPatternDetails matchPatternDetails) {
            if (value == null || value.equals(""))
                return RestServiceActionLocator.RestMatchRating.None;
            if (!matchPatternDetails.isId())
                return RestServiceActionLocator.RestMatchRating.None;
            return identityLookup.find(value).isPresent() ? RestServiceActionLocator.RestMatchRating.Perfect : RestServiceActionLocator.RestMatchRating.Vague;
        }

        @Override
        public <E extends Identity> Optional<E> getIdentity() {
            return  identityLookup.find(value).map(identity -> (E) identity);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class Indexer {
        int index = 0;
        RestServiceActionLocator.IdentityLookup identityLookup;

        public Indexer(RestServiceActionLocator.IdentityLookup identityLookup) {
            this.identityLookup = identityLookup;
        }

        RestPathValue createRestPathValue(String value) {
            return new RestPathValueImpl(index++, value, identityLookup);
        }
    }
}
