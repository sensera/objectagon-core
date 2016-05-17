package org.objectagon.core.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-16.
 */
public class FormatterImpl implements Formatter {

    final static String SPACE = "                                                                                                                                            ";

    public static Format standard() {
        return new FormatterImpl().create()
                .levelColor(Level.TRACE,ANSI_BLUE)
                .levelColor(Level.DEBUG,ANSI_BLUE)
                .levelColor(Level.INFO,ANSI_GREEN)
                .levelColor(Level.WARN,ANSI_YELLOW)
                .levelColor(Level.ERROR, ANSI_RED)
                .addColumn(ColumnType.CLASS, 40)
                .addColumn(ColumnType.METHOD, 15)
                .addColumn(ColumnType.LEVEL, 6)
                .addColumn(ColumnType.ERROR, 200, true)
                .addColumn(ColumnType.MESSAGE, 200)
                .build();
    }

    @Override
    public FormatBuilder create() {
        return new FormatBuilderImpl();
    }

    private static class FormatBuilderImpl implements FormatBuilder {
        String baseColor = ANSI_BLACK;
        List<Column> columns = new ArrayList<>();
        List<LevelColor> levelColors = new ArrayList<>();

        @Override
        public FormatBuilder levelColor(Level level, String color) {
            levelColors.add(new LevelColor(level, color));
            return this;
        }

        @Override
        public FormatBuilder addColumn(ColumnType columnType, int width) {
            columns.add(new Column(columnType, width, false));
            return this;
        }

        @Override
        public FormatBuilder addColumn(ColumnType columnType, int width, boolean autoHide) {
            columns.add(new Column(columnType, width, autoHide));
            return this;
        }

        @Override
        public Format build() {
            return new FormatImpl(baseColor, columns, levelColors.stream());
        }
    }

    private static class FormatImpl implements Format {
        private String baseColor = ANSI_BLACK;
        private List<Column> columns = new ArrayList<>();
        Map<Level, String> levelColors = new HashMap<>();

        public FormatImpl(String baseColor, List<Column> columns, Stream<LevelColor> levelColors) {
            this.baseColor = baseColor;
            this.columns = columns;
            levelColors.forEach(levelColor -> levelColor.put(this.levelColors));
        }

        @Override
        public void row(ValueBuilder valueBuilder) {
            ValueImpl value = new ValueImpl();
            valueBuilder.build(value);
            final StringBuffer sb = new StringBuffer();
            value.level.ifPresent(level -> sb.append(levelColors.getOrDefault(level, baseColor)));

            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_DATE_TIME;

            for (int col = 0; columns.size() <= col; col++) {
                final Column column = columns.get(col);
                column.print(sb, value, dateTimeFormat);
            }

            sb.append(baseColor);
        }


    }

    private static class ValueImpl implements Value {
        private Optional<String> className = Optional.empty();
        private Optional<String> methodName = Optional.empty();
        private Optional<Level> level = Optional.empty();
        private Optional<String> message = Optional.empty();
        private Optional<String> notis = Optional.empty();
        private Optional<LocalTime> time = Optional.empty();
        private Optional<Throwable> error = Optional.empty();

        @Override
        public Value clazz(String className) {
            this.className = Optional.of(className);
            return this;
        }

        @Override
        public Value method(String methodName) {
            this.methodName = Optional.of(methodName);
            return this;
        }

        @Override
        public Value level(Level level) {
            this.level = Optional.of(level);
            return this;
        }

        @Override
        public Value message(String message) {
            this.message = Optional.of(message);
            return this;
        }

        @Override
        public Value notis(String notis) {
            this.notis = Optional.of(notis);
            return this;
        }

        @Override
        public Value time(LocalTime time) {
            this.time = Optional.of(time);
            return this;
        }

        @Override
        public Value error(Throwable throwable) {
            this.error = Optional.of(throwable);
            return this;
        }
    }

    private static class Column {
        private final ColumnType columnType;
        private final int width;
        private final boolean autoHide;

        public ColumnType getColumnType() {
            return columnType;
        }

        public int getWidth() {
            return width;
        }

        public boolean isAutoHide() {
            return autoHide;
        }

        public Column(ColumnType columnType, int width, boolean autoHide) {
            this.columnType = columnType;
            this.width = width;
            this.autoHide = autoHide;
        }

        void print(StringBuffer sb, ValueImpl value, DateTimeFormatter timeformat) {
            switch (columnType) {
                case CLASS: value.className.ifPresent(printString(sb)); break;
                case METHOD: value.methodName.ifPresent(printString(sb)); break;
                case LEVEL: value.level.ifPresent(printLevel(sb)); break;
                case MESSAGE: value.message.ifPresent(printString(sb)); break;
                case NOTIS: value.notis.ifPresent(printString(sb)); break;
                case TIME:
                    value.time.ifPresent(printTime(sb, timeformat));
                    if (!value.time.isPresent())
                        printTime(sb, timeformat).accept(LocalTime.now());
                    break;
                default: throw new RuntimeException("INTERNAL ERROR");
            }
        }

        Consumer<String> printString(StringBuffer sb) {
            return s -> sb.append(fixate(s));
        }

        Consumer<Level> printLevel(StringBuffer sb) {
            return s -> sb.append(fixate(levelToString(s)));
        }

        Consumer<LocalTime> printTime(StringBuffer sb, DateTimeFormatter timeformat) {
            return s -> sb.append(fixate(s.format(timeformat)));
        }

        private String fixate(String output) {
            if (autoHide && output.isEmpty())
                return "";
            if (output.length() > width)
                return output.substring(0,width);
            if (output.length() < width)
                return output + SPACE.substring(0,width-output.length());
            return output;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Column)) return false;
            Column column = (Column) o;
            return getWidth() == column.getWidth() &&
                    isAutoHide() == column.isAutoHide() &&
                    getColumnType() == column.getColumnType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getColumnType(), getWidth(), isAutoHide());
        }

        @Override
        public String toString() {
            return "Column{" +
                    "columnType=" + columnType +
                    ", width=" + width +
                    ", autoHide=" + autoHide +
                    '}';
        }
    }

    private static String levelToString(Level level) {
        return level.name();
    }

    private static class LevelColor {
        private final Level level;
        private final String color;

        public LevelColor(Level level, String color) {
            this.level = level;
            this.color = color;
        }

        public void put(Map<Level, String> map) {
            map.put(level, color);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LevelColor)) return false;
            LevelColor that = (LevelColor) o;
            return level == that.level &&
                    Objects.equals(color, that.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, color);
        }

        @Override
        public String toString() {
            return "LevelColor{" +
                    "level=" + level +
                    ", color='" + color + '\'' +
                    '}';
        }
    }
}
