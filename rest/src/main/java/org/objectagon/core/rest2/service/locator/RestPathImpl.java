package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathValue;
import org.objectagon.core.storage.Identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class RestPathImpl implements RestServiceActionLocator.RestPath {

    public static RestServiceActionLocator.RestPath create(Name path, RestServiceActionLocator.RestSession identityLookup) {
        Indexer indexer = new Indexer(identityLookup);
        if (path == null || path.toString().equals("/") || path.toString().equals(""))
            return new RestPathImpl(identityLookup, Collections.EMPTY_LIST);
        return new RestPathImpl(identityLookup, Stream.of(path.toString().split("/"))
                .filter(s -> !s.isEmpty())
                .map(indexer::createRestPathValue)
                .collect(Collectors.toList()));
    }

    private List<RestPathValue> pathValues = new ArrayList<>();
    private RestServiceActionLocator.RestSession identityLookup;

    public RestPathImpl(RestServiceActionLocator.RestSession identityLookup, List<RestPathValue> pathValues) {
        this.pathValues = pathValues;
        this.identityLookup = identityLookup;
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
    public <E extends Identity> Optional<E> getIdentityAtIndex(int index) {
        return pathValues.get(index).getIdentity();
    }

    @Override
    public <E extends Name> Optional<E> getNameAtIndex(int index, RestServiceActionLocator.RestPathName restPathName) {
        return pathValues.get(index).getName(restPathName);
    }

    @Override
    public int size() {
        return pathValues.size();
    }

    @Override public <I extends Identity> Optional<I> getIdentityByAlias(String alias) {
        return identityLookup.getIdentityByAlias(alias).map(identity -> (I) identity);
    }

    private static class RestPathValueImpl implements RestPathValue {
        private int index;
        private String value;
        RestServiceActionLocator.RestSession identityLookup;

        public RestPathValueImpl(int index, String value, RestServiceActionLocator.RestSession identityLookup) {
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
            return identityLookup.getIdentityByAlias(value).isPresent() ? RestServiceActionLocator.RestMatchRating.Perfect : RestServiceActionLocator.RestMatchRating.Vague;
        }

        @Override
        public <E extends Identity> Optional<E> getIdentity() {
            return  identityLookup.getIdentityByAlias(value).map(identity -> (E) identity);
        }

        @Override
        public <E extends Name> Optional<E> getName(RestServiceActionLocator.RestPathName restPathName) {
            return Optional.of( (E) restPathName.createNameFromText(value));
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class Indexer {
        int index = 0;
        RestServiceActionLocator.RestSession identityLookup;

        public Indexer(RestServiceActionLocator.RestSession identityLookup) {
            this.identityLookup = identityLookup;
        }

        RestPathValue createRestPathValue(String value) {
            return new RestPathValueImpl(index++, value, identityLookup);
        }
    }
}
