package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.rest2.service.RestServiceActionLocator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by christian on 2017-02-26.
 */
public class RestPathPatternBuilderImpl implements RestServiceActionLocator.RestPathPatternBuilder {

    public static RestServiceActionLocator.RestPathPatternBuilder create() { return new RestPathPatternBuilderImpl();}

    public static RestServiceActionLocator.RestPathPatternBuilder base(String name) { return new RestPathPatternBuilderImpl().text(name);}

    public static RestServiceActionLocator.RestPathPattern urlPattern(String input) {
        final RestPathPatternBuilderSequence restPathPatternBuilderSequence = new RestPathPatternBuilderSequence();
        Arrays.stream(input.split("/"))
                .map(String::trim)
                .forEach(restPathPatternBuilderSequence::parse);
        return restPathPatternBuilderSequence.build();
    }

    private List<RestPathPatternImpl.MatchPatternDetails> patterns = new LinkedList<>();

    private RestPathPatternBuilderImpl() {}

    @Override
    public RestServiceActionLocator.RestPathPattern build() {
        return RestPathPatternImpl.create(patterns);
    }

    @Override
    public RestServiceActionLocator.RestPathPatternBuilder text(String text) {
        patterns.add(new TextPattern(text));
        return this;
    }

    @Override
    public RestServiceActionLocator.RestPathPatternBuilder id() {
        patterns.add(new IdPattern());
        return this;
    }

    @Override
    public RestServiceActionLocator.RestPathPatternBuilder name() {
        patterns.add(new NamePattern());
        return this;
    }

    @Override
    public RestServiceActionLocator.RestPathPatternBuilder parsePart(String input) {
        if (input.equalsIgnoreCase("{id}"))
            return id();
        if (input.equalsIgnoreCase("{name}"))
            return name();
        return text(input);
    }

    private class TextPattern implements RestPathPatternImpl.MatchPatternDetails {

        private String text;

        public TextPattern(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public boolean isText() {
            return true;
        }

        @Override
        public boolean isId() {
            return false;
        }

        @Override
        public boolean isName() {
            return false;
        }
    }

    private class IdPattern implements RestPathPatternImpl.MatchPatternDetails {
        @Override
        public String getText() {
            return null;
        }

        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public boolean isId() {
            return true;
        }

        @Override
        public boolean isName() {
            return false;
        }
    }

    private class NamePattern implements RestPathPatternImpl.MatchPatternDetails {
        @Override
        public String getText() {
            return null;
        }

        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public boolean isId() {
            return true;
        }

        @Override
        public boolean isName() {
            return true;
        }
    }

    static class RestPathPatternBuilderSequence {
        RestServiceActionLocator.RestPathPatternBuilder builder;

        RestPathPatternBuilderSequence() {
            this.builder = new RestPathPatternBuilderImpl();
        }

        void parse(String input) {
            builder = selectAction(input).apply(builder);
        }

        public RestServiceActionLocator.RestPathPattern build() {
            return builder.build();
        }

        private Function<RestServiceActionLocator.RestPathPatternBuilder, RestServiceActionLocator.RestPathPatternBuilder> selectAction(String input) {
            if (input.isEmpty())
                return restPathPatternBuilder -> restPathPatternBuilder;
            if (input.equalsIgnoreCase("{id}")) {
                return RestServiceActionLocator.RestPathPatternBuilder::id;
            } else if (input.equalsIgnoreCase("{name}")) {
                return RestServiceActionLocator.RestPathPatternBuilder::name;
            }
            return restPathPatternBuilder -> restPathPatternBuilder.text(input);
        }
    }


}
