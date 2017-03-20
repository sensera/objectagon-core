package org.objectagon.core.rest2.utils;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.name.StandardName;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 2016-05-06.
 */
public class JsonBuilderImpl implements JsonBuilder {
    private static String JSON_SPACE = "                                                                                                                               " +
            "                                                                                                                                                          ";

    @Override
    public Builder builder() {
        return new LocalBuilder();
    }

    private class LocalBuilder extends LocalItem implements Builder {

        public LocalBuilder() {
            super(0);
        }

        @Override
        public Json build() {
            JsonFormatter sf = new JsonFormatter();
            build(sf);
            return new LocalJson(sf.buffer());
        }
    }

    private class LocalItem implements Item {
        private final int level;
        String value;
        Map<Name,List<LocalItem>> children = new HashMap<>();

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        public LocalItem(int level) {
            this.level = level;
        }

        private LocalItem addChild(Name name, LocalItem localItem) {
            List<LocalItem> items = children.get(name);
            if (items==null) {
                items = new ArrayList<>();
                children.put(name, items);
            }
            items.add(localItem);
            return localItem;
        }

        @Override
        public void setChildValue(Name name, String value) {
            addChild(name, new LocalItem(level+1)).setValue(value);
        }

        @Override
        public Item addChild(Name name) {
            return addChild(name, new LocalItem(level+1));
        }

        @Override
        public Message.BuilderValue create(String name) {
            return addChild(StandardName.name(name));
        }

        @Override
        public Message.BuilderValue create(Message.Field field) {
            return addChild(field.getName());
        }

        @Override
        public Message.BuilderItem values(String name) {
            return addChild(StandardName.name(name));
        }

        @Override
        public Message.BuilderItem values() {
            return addChild(StandardName.name("VALUES"));
        }

        @Override
        public void set(String value) {
            this.value = value;
        }

        @Override
        public void set(Long value) {
            this.value = ""+value;
        }

        @Override
        public void set(Message.Value value) {
            //TODO implement correct
            this.value = "Not implemented";
            throw new RuntimeException("Not implemented!");
        }

        @Override
        public void set(Message value) {
            //TODO implement correct
            this.value = value.getName().toString();
        }

        @Override
        public void set(Message.MessageName value) {
            this.value = value.toString();
        }

        @Override
        public void set(Name value) {
            this.value = value.toString();
        }

        @Override
        public void set(Address value) {
            //TODO implement correct
            this.value = value.toString();
        }

        @Override
        public Message.BuilderItem values(Message.Field field) {
            return addChild(field.getName());
        }

        public void build(JsonFormatter json) {
            if (value != null) {
                json.setValue(value);
            } else {
                if (children.isEmpty())
                    return;
                json.startBrace();
                children.entrySet().stream().forEach(stringListEntry -> {
                    json.setKey(level, Name.getNameAsString(stringListEntry.getKey()));
                    List<LocalItem> value = stringListEntry.getValue();
                    if (value.size()==1) {
                        value.get(0).build(json);
                    } else {
                        int bracketIndex = json.startBracket();
                        value.stream().forEach(localItem -> {
                            json.addComma(bracketIndex);
                            localItem.build(json);
                        });
                        json.endBracket(level);
                    }
                });
                json.endBrace(level);
            }
        }
    }

    private class JsonFormatter {
        StringBuilder json = new StringBuilder();
        void append(int level, String value) {
            json.append(JSON_SPACE.substring(0,level*4)+value);
        }
        Map<Integer, Boolean> bracketIndex = new HashMap<>();

        void append(String value) {
            json.append(value);
        }

        public byte[] buffer() {
            return json.toString().getBytes();
        }

        public void setValue(String value) {
            append("\""+value+"\"\n");
        }

        public void startBrace() {
            append("{\n");
        }

        public void endBrace(int level) {
            append(level, "}\n");
        }

        public void setKey(int level, String key) {
            append(level, "\""+ key + "\"" + ": ");
        }

        public int startBracket() {
            append("[\n");
            int size = bracketIndex.size();
            bracketIndex.put(size, false);
            return size;
        }

        public void endBracket(int level) {
            append(level, "]");
        }

        public void addComma(int bracketIndex) {
            if (this.bracketIndex.get(bracketIndex)) {
                append(", ");
            } else {
                this.bracketIndex.put(bracketIndex, true);
            }
        }
    }

    private class LocalJson implements Json {
        private final byte[] data;

        public LocalJson(byte[] data) {
            this.data = data;
        }

        @Override
        public long size() {
            return data.length;
        }

        @Override
        public void write(OutputStream outputStream) throws IOException {
            outputStream.write(data);
        }

        @Override
        public void write(ByteBuf byteBuf) {
            byteBuf.setBytes(0,data);
        }

        @Override
        public String toString() {
            return new String(data);
        }
    }
}
