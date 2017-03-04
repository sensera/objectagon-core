package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.msg.Name;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathValue;
import org.objectagon.core.storage.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class RestPathImpl implements RestServiceActionLocator.RestPath {

    public static RestServiceActionLocator.RestPath create(Name path, RestServiceActionLocator.IdentityLookup identityLookup) {
        Indexer indexer = new Indexer(identityLookup);
        return new RestPathImpl(Stream.of(path.toString().split("/"))
                .map(indexer::createRestPathValue)
                .collect(Collectors.toList()));
    }

    private List<RestPathValue> pathValues = new ArrayList<>();

    public RestPathImpl(List<RestPathValue> pathValues) {
        this.pathValues = pathValues;
    }

    public Stream<RestPathValue> values() { return pathValues.stream(); }

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
            if (matchPatternDetails.getText().equalsIgnoreCase(matchPatternDetails.getText()))
                return RestServiceActionLocator.RestMatchRating.Perfect;
            return RestServiceActionLocator.RestMatchRating.None;
        }

        @Override
        public RestServiceActionLocator.RestMatchRating isId() {
            return identityLookup.find(value).isPresent() ? RestServiceActionLocator.RestMatchRating.Perfect : RestServiceActionLocator.RestMatchRating.Vague;
        }

        @Override
        public <E extends Identity> Optional<E> getIdentity() {
            return  identityLookup.find(value).map(identity -> (E) identity);
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
