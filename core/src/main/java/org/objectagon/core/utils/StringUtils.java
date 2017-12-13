package org.objectagon.core.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * Created by christian on 2017-09-08.
 */
public class StringUtils {

    public static Optional<String> extractStringWithinMarkers(
            String marker,
            String text,
            int index,
            Function<Integer,Optional<Integer>> preProcessText,
            Function<String,String> postProcessorText) {
        if (!Objects.equals(text.substring(index,index+marker.length()),marker)) {
            return Optional.empty();
        }
        index = index + marker.length();
        int indexOfThis = text.indexOf(marker, preProcessText(index, preProcessText) );
        if (indexOfThis == -1)
            //return Optional.of(text.substring(index, index + marker.length()));
            throw new RuntimeException("Internal error. Unterminated \" at " + index);
        String st = text.substring(index, indexOfThis);
        st = postProcessorText != null ? postProcessorText.apply(st) : st;
        //return st.equals("") ? Optional.empty() : Optional.ofNullable(st);
        return Optional.ofNullable(st);
    }

    static int preProcessText(int index, Function<Integer, Optional<Integer>> preProcessText) {
        if (preProcessText == null)
            return index;
        Optional<Integer> newIndex = preProcessText.apply(index);
        while (newIndex.isPresent()) {
            index = newIndex.get();
            newIndex = preProcessText.apply(index);
        }
        return index;
    }

    public static Function<Integer, Optional<Integer>> forwardIndexToIgnoreMarkedMarkers(String text, String marker, String ignoreMarker) {
        return index -> {
            int indexOfIgnore = text.indexOf(ignoreMarker+marker, index);
            if (indexOfIgnore == -1)
                return Optional.empty();
            int indexOfMarker = text.indexOf(marker, index);
            if (indexOfMarker < indexOfIgnore) {
                return Optional.empty();
            }
            return Optional.of(indexOfIgnore + marker.length() + ignoreMarker.length());
        };
    }

    public static Function<String,String> replace(String marker, String ignoreMarker) {
        return text -> Objects.equals(ignoreMarker, "\\") ? alternateReplace(text, ignoreMarker+marker, marker) : text.replaceAll(ignoreMarker+marker, marker);
    }

    public static String alternateReplace(String text, String replace, String with) {
        StringBuffer sb = new StringBuffer();
        final StringTokenizer stringTokenizer = new StringTokenizer(text, replace);
        if (!stringTokenizer.hasMoreTokens())
            return text;
        sb.append(stringTokenizer.nextToken());
        while (stringTokenizer.hasMoreTokens()) {
            sb.append(with);
            sb.append(stringTokenizer.nextToken());
        }
        if (text.endsWith(with))
            sb.append(with);
        return sb.toString();
    }


}
