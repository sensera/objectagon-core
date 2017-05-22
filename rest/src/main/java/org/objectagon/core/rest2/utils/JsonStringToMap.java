package org.objectagon.core.rest2.utils;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;

import java.util.*;

/**
 * Created by christian on 2017-04-17.
 */
public class JsonStringToMap {

    public static <N extends Name, V extends Message.Value> Map<N,V> parse(String json) {
/*
        System.out.println("JsonStringToMap.parse ---------------------------------------------");
        System.out.println(json);
        System.out.println("JsonStringToMap.parse ---------------------------------------------");
*/
        Map<N,V> res = new HashMap<>();
        JsonTokenizer jsonTokenizer = new JsonTokenizerImpl(json);

        Optional<JsonToken> jsonToken = jsonTokenizer.next();
        if (!jsonToken.isPresent())
            return res;
        switch (jsonToken.get().getType()) {
            case JSON_START: {
                jsonToken = jsonTokenizer.next();
                if (validateToken(jsonToken, JsonTokenType.KEY)) {
                    N name = (N) StandardName.name(jsonToken.get().getText());
                    jsonToken = jsonTokenizer.next();
                    jsonToken.ifPresent(jt -> {
                        switch (jt.getType()) {
                            case JSON_START: res.put(name, createMap(jt, jsonTokenizer)); break;
                            case LIST_START: res.put(name, createList(jt, jsonTokenizer)); break;
                            case VALUE: res.put(name, (V) MessageValue.text(jt.getText())); break;
                            default: throw new RuntimeException("Error parsing json! Expected JSON_START, LIST_START or VALUE not "+jt.getType()+" with value "+jt.getText());
                        }
                    });
                }
                break;
            }
            case LIST_START: {
                res.put( (N) StandardName.name("root"), createList(jsonToken.get(), jsonTokenizer)); break;
            }
            default: throw new RuntimeException("Error parsing json! Expected JSON_START or LIST_START  not "+jsonToken.get().getType()+" with value "+jsonToken.get().getText());
        }
        return res;
    }

    private static <V extends Message.Value> V createList(JsonToken jsonToken, JsonTokenizer jsonTokenizer) {
        //System.out.println("JsonStringToMap.createList [");
        if (jsonToken==null)
            throw new RuntimeException("Internal error creating list! No token");
        if (!jsonToken.getType().equals(JsonTokenType.LIST_START))
            throw new RuntimeException("Internal error creating list! Not list token! "+jsonToken.getType()+" "+jsonToken.getText());
        jsonToken = jsonTokenizer.next().orElse(null);
        List<Message.Value> values = new ArrayList<>();
        while (jsonToken != null) {
            if (jsonToken.getType().equals(JsonTokenType.LIST_END)) {
                break;
            }
            if (!jsonToken.getType().equals(JsonTokenType.JSON_START)) {
                throw new RuntimeException("Internal error Expected JSON_START but found "+jsonToken.getType()+" "+jsonToken.getText());
            }
            values.add(createMap(jsonToken, jsonTokenizer));
            jsonToken = jsonTokenizer.next().orElse(null);
        }
        //System.out.println("JsonStringToMap.createList ]");
        return (V) MessageValue.values(values);
    }

    private static <V extends Message.Value> V createMap(JsonToken jsonToken, JsonTokenizer jsonTokenizer) {
        //System.out.println("JsonStringToMap.createMap "+jsonToken.getText());
        if (jsonToken == null)
            throw new RuntimeException("Internal error creating map! No token");
        if (!jsonToken.getType().equals(JsonTokenType.JSON_START))
            throw new RuntimeException("Internal error creating map! Not map token! "+jsonToken.getType()+" "+jsonToken.getText());
        jsonToken = jsonTokenizer.next().orElse(null);
        Map<Name,Message.Value> res = new HashMap<>();
        while (jsonToken != null) {
            if (jsonToken.getType().equals(JsonTokenType.JSON_END)) {
                break;
            }
            if (!jsonToken.getType().equals(JsonTokenType.KEY)) {
                throw new RuntimeException("Internal error creating map! Not key token! "+jsonToken.getType()+" "+jsonToken.getText());
            }
            Name name = StandardName.name(jsonToken.getText());
            jsonToken = jsonTokenizer.next().orElse(null);
            if (jsonToken == null)
                throw new RuntimeException("Internal error creating map! Expected token after key!");
            if (jsonToken.getType().equals(JsonTokenType.VALUE)) {
                res.put(name, MessageValue.text(jsonToken.getText()));
            } else if (jsonToken.getType().equals(JsonTokenType.JSON_START)) {
                res.put(name, createMap(jsonToken, jsonTokenizer));
            } else if (jsonToken.getType().equals(JsonTokenType.LIST_START)) {
                res.put(name, createList(jsonToken, jsonTokenizer));
            } else {
                //System.out.println("JsonStringToMap.createMap name="+name);
                throw new RuntimeException("Internal error creating map! Unexpected token! "+jsonToken.getType()+" "+jsonToken.getText());
            }
            jsonToken = jsonTokenizer.next().orElse(null);
        }
        return (V) MessageValue.map(res);
    }

    private static Boolean validateToken(Optional<JsonToken> jsonToken, JsonTokenType jsonTokenTypeCheck) {
        return jsonToken.map(JsonToken::getType).map(jsonTokenType -> jsonTokenType.equals(jsonTokenTypeCheck)).orElse(false);
    }

    enum JsonTokenType {
        JSON_START,
        LIST_START,
        KEY,
        VALUE,
        JSON_END,
        LIST_END
    }

    interface JsonToken {
        JsonTokenType getType();
        String getText();
    }

    interface JsonTokenizer {
        Optional<JsonToken> next();
    }

    static class JsonTokenizerImpl implements JsonTokenizer {
        String json;
        int index = 0;
        boolean key = false;

        public JsonTokenizerImpl(String json) {
            this.json = json;
        }

        @Override
        public Optional<JsonToken> next() {
            Optional<String> startCharOptional = approvedStartChar();
            if (isEnd())
                return Optional.empty();
            while (!startCharOptional.isPresent()) {
                nextIndex();
                if (isEnd())
                    return Optional.empty();
                startCharOptional = approvedStartChar();
            }
            final String st = startCharOptional.get();
            if (st.equals("{")) {
                key = false;
                nextIndex();
                return Optional.of(new MyJsonToken(JsonTokenType.JSON_START, st));
            }
            if (st.equals("[")) {
                key = false;
                nextIndex();
                return Optional.of(new MyJsonToken(JsonTokenType.LIST_START, st));
            }
            if (st.equals("}")) {
                key = false;
                nextIndex();
                return Optional.of(new MyJsonToken(JsonTokenType.JSON_END, st));
            }
            if (st.equals("]")) {
                key = false;
                nextIndex();
                return Optional.of(new MyJsonToken(JsonTokenType.LIST_END, st));
            }
            if (!key) {
                key = true;
                index += st.length() + 2;
                return Optional.of(new MyJsonToken(JsonTokenType.KEY, st));
            } else {
                key = false;
                index += st.length() + 2;
                return Optional.of(new MyJsonToken(JsonTokenType.VALUE, st));
            }
        }

        private void nextIndex() {
            index++;
        }

        private boolean isEnd() {
            return index == json.length();
        }

        private Optional<String> approvedStartChar() {
            if (isEnd())
                return Optional.empty();
            String jsonChar = json.substring(index,index+1);
            if (jsonChar.equals("{")
                    || jsonChar.equals("}")
                    || jsonChar.equals("[")
                    || jsonChar.equals("]")
                    )
                return Optional.of(jsonChar);
            if (jsonChar.equals("'")) {
                int indexOfThis = json.indexOf("'", index+1);
                if (indexOfThis == -1)
                    throw new RuntimeException("Internal error. Unterminated \" at "+index);
                String st = json.substring(index + 1, indexOfThis);
                return Optional.of(st);
            }
            if (jsonChar.equals("\"")) {
                int indexOfThis = json.indexOf("\"", index+1);
                if (indexOfThis == -1)
                    throw new RuntimeException("Internal error. Unterminated \" at "+index);
                String st = json.substring(index + 1, indexOfThis);
                return Optional.of(st);
            }
            return Optional.empty();
        }

        private class MyJsonToken implements JsonToken {

            private final String text;
            private final JsonTokenType type;

            public MyJsonToken(JsonTokenType type, String text) {
                this.type = type;
                this.text = text;
            }

            @Override public JsonTokenType getType() {return type;}
            @Override public String getText() {return text;}

            @Override
            public String toString() {
                return "MyJsonToken{" +
                        "text='" + text + '\'' +
                        ", type=" + type +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "JsonTokenizerImpl{" +
                    "index=" + index +
                    ", key=" + key +
                    '}';
        }
    }
}
