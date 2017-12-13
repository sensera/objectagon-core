package org.objectagon.core.utils;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by christian on 2017-09-08.
 */
public class StringUtilsTest {

    private String marker = "\"";

    @Test public void testMainPositiveExamples() {
        assertThat(StringUtils.extractStringWithinMarkers(marker, "\"Hello\"", 0, null, null).get(), is("Hello"));
        assertThat(StringUtils.extractStringWithinMarkers(marker, "Hubber \"Hello\"", 7, null, null).get(), is("Hello"));
        assertThat(StringUtils.extractStringWithinMarkers(marker, "Jibberish \"Jello\" Hubber \" J Ji \"Hello\" lklkf \"JJek\"", 32, null, null).get(), is("Hello"));
    }

    @Test(expected = RuntimeException.class) public void testMissingSecondBracket() {
        StringUtils.extractStringWithinMarkers(marker, "\"Hello", 0, null, null);
    }

    @Test() public void testMoMatch() {
        assertThat(StringUtils.extractStringWithinMarkers("'", "\"Hello\"", 1, null, null).isPresent(), is(false));
    }

    @Test public void testSingleIgnore() {
        final Function<Integer, Optional<Integer>> integerOptionalFunction = StringUtils.forwardIndexToIgnoreMarkedMarkers("Gub\\\"Hyy\"NNh", "\"", "\\");
        assertEquals(5, integerOptionalFunction.apply(1).get().intValue());
        assertFalse(integerOptionalFunction.apply(5).isPresent());
    }

    @Test public void testMultipleIgnore() {
        final Function<Integer, Optional<Integer>> integerOptionalFunction = StringUtils.forwardIndexToIgnoreMarkedMarkers("Gub\\\"Hy kke \\\"kkke y\"NNh", "\"", "\\");
        assertEquals(5, integerOptionalFunction.apply(1).get().intValue());
        assertEquals(14, integerOptionalFunction.apply(5).get().intValue());
        assertFalse(integerOptionalFunction.apply(14).isPresent());
    }

    Function<Integer, Optional<Integer>> addIndexWIth4Max10 = index -> (index < 10 ? Optional.of(new Integer(index + 4)) : Optional.empty());

    @Test public void testPreProcessText() {
        assertEquals(13, StringUtils.preProcessText(1, addIndexWIth4Max10));
        assertEquals(10, StringUtils.preProcessText(2, addIndexWIth4Max10));
    }

    @Test public void testReplace() {
        assertEquals("Hubba", StringUtils.replace(marker, "\\").apply("Hubba"));
        assertEquals("Hu\"bba", StringUtils.replace(marker, "X").apply("HuX\"bba"));
        assertEquals("Hu\"bba", StringUtils.replace(marker, "\\").apply("Hu\\\"bba"));
        assertEquals("meta \"Lo\"", StringUtils.replace(marker, "\\").apply("meta \"Lo\""));
        assertEquals("meta \"Lo\" Kanna", StringUtils.replace(marker, "\\").apply("meta \"Lo\" Kanna"));
    }
}