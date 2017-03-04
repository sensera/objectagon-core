package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.rest2.service.RestServiceActionLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-02-22.
 */
public class RestPathPatternImpl implements RestServiceActionLocator.RestPathPattern {

    public static RestServiceActionLocator.RestPathPattern create(List<MatchPatternDetails> matches) {
        return new RestPathPatternImpl(matches);
    }

    private static final Predicate<RestServiceActionLocator.RestMatchRating> NONE =
            restMatchRating -> restMatchRating.equals(RestServiceActionLocator.RestMatchRating.None);
    private static final Predicate<RestServiceActionLocator.RestMatchRating> VAGUE =
            restMatchRating -> restMatchRating.equals(RestServiceActionLocator.RestMatchRating.Vague);
    private static final Predicate<RestServiceActionLocator.RestMatchRating> OK =
            restMatchRating -> restMatchRating.equals(RestServiceActionLocator.RestMatchRating.Ok);

    private List<MatchPattern> matches = new ArrayList<>();

    public RestPathPatternImpl(List<MatchPatternDetails> matches) {
        this.matches = matches.stream()
                .map(RestPathPatternImpl::createPatternMatchFromDetails)
                .collect(Collectors.toList());
    }

    private static MatchPattern createPatternMatchFromDetails(MatchPatternDetails matchPatternDetails) {
        return restPathValue -> {
            if (matchPatternDetails.isName())
                return RestServiceActionLocator.RestMatchRating.Ok;
            RestServiceActionLocator.RestMatchRating ratingText = restPathValue.isText(matchPatternDetails);
            switch (ratingText) {
                case Perfect:
                case Ok: return RestServiceActionLocator.RestMatchRating.Ok;
            }
            RestServiceActionLocator.RestMatchRating ratingId = restPathValue.isId();
            switch (ratingId) {
                case Perfect:
                case Ok: return RestServiceActionLocator.RestMatchRating.Ok;
            }
            return matchPatternDetails.min(ratingId, ratingText);
        };
    }

    @Override
    public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
        final List<RestServiceActionLocator.RestMatchRating> matching = restPath.values()
                .map(restPathValue -> matches.get(restPathValue.getIndex()).check(restPathValue))
                .collect(Collectors.toList());
        if (matching.stream().anyMatch(NONE))
            return RestServiceActionLocator.RestMatchRating.None;
        if (matching.stream().anyMatch(VAGUE))
            return RestServiceActionLocator.RestMatchRating.Vague;
        if (matching.stream().anyMatch(OK))
            return RestServiceActionLocator.RestMatchRating.Ok;
        return RestServiceActionLocator.RestMatchRating.Perfect;
    }

    interface MatchPattern {
        RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPathValue restPathValue);
    }

    public interface MatchPatternDetails {
        String getText();
        boolean isText();
        boolean isId();
        boolean isName();

        default RestServiceActionLocator.RestMatchRating max(RestServiceActionLocator.RestMatchRating... list) {
            return Arrays.stream(list).max(Comparator.naturalOrder()).orElse(RestServiceActionLocator.RestMatchRating.Perfect);
        }

        default RestServiceActionLocator.RestMatchRating min(RestServiceActionLocator.RestMatchRating... list) {
            return Arrays.stream(list).min(Comparator.naturalOrder()).orElse(RestServiceActionLocator.RestMatchRating.None);
        }
    }
}
