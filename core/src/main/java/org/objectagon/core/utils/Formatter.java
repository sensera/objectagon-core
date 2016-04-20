package org.objectagon.core.utils;

import java.time.LocalTime;

/**
 * Created by christian on 2016-04-16.
 */
public interface Formatter {

    enum ColumnType {
        CLASS, METHOD, LEVEL, MESSAGE, TIME, NOTIS, ERROR
    }

    enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    String ANSI_RESET = "\u001B[0m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";

    FormatBuilder create();

    interface FormatBuilder {
        FormatBuilder addColumn(ColumnType columnType, int width);
        FormatBuilder addColumn(ColumnType columnType, int width, boolean autoHide);
        FormatBuilder levelColor(Level level, String color);
        Format build();
    }

    interface Format {
        void row(ValueBuilder valueBuilder);
    }

    interface Value {
        Value clazz(String className);
        Value method(String methodName);
        Value level(Level level);
        Value message(String message);
        Value notis(String message);
        Value time(LocalTime time);
        Value error(Throwable throwable);
    }

    @FunctionalInterface
    interface ValueBuilder {
        void build(Value value);
    }

}
