package org.objectagon.core.rest2.utils;

import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.utils.Util;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-04-17.
 */
public class JsonStringToMapTest {

    String create_person_method_code = "invokeWorker.setValue(\"sumValue\").set(invokeWorker.getValue(\"sumValue\").asNumber() + invokeWorker.getValue(\"addValue\").asNumber());";

    String create_domain_json = "[\n" +
            "    {'main.meta': {\n" +
            "        'type': 'meta',\n" +
            "                'methods': [\n" +
            "        {'create.person.method': {\n" +
            "            'code': '"+create_person_method_code+"'\n" +
            "        }}\n" +
            "        ]\n" +
            "    }},\n" +
            "    {'main.class': {\n" +
            "        'type': 'class',\n" +
            "                'relations': [\n" +
            "        {'main.person.relation': {\n" +
            "            'targetClass': 'person.class'\n" +
            "        }}\n" +
            "        ]\n" +
            "    }},\n" +
            "    {'person.class': {\n" +
            "        'type': 'class',\n" +
            "                'fields': [\n" +
            "        {'Name': {\n" +
            "            'type': 'Text'\n" +
            "        }},\n" +
            "        {'Age': {\n" +
            "            'type': 'Number'\n" +
            "        }},\n" +
            "        {'Comment': {\n" +
            "            'type': 'Text'\n" +
            "        }}\n" +
            "        ]\n" +
            "    }}\n" +
            "    ]";

    String simpleJson =
            "{\"person.class\": {\n" +
            "    \"type\": \"class\",\n" +
            "            \"fields\": [\n" +
            "    {\"Name\": {\n" +
            "        \"type\": \"Text\"\n" +
            "    }},\n" +
            "    {\"Age\": {\n" +
            "        \"type\": \"Number\"\n" +
            "    }},\n" +
            "    {\"Comment\": {\n" +
            "        \"type\": \"Text\"\n" +
            "    }}\n" +
            "    ]\n" +
            "}}\n";


    @Test
    public void parseSimplaOneFieldJson() throws Exception {
        final Map<? extends Name, ? extends Message.Value> parsed = JsonStringToMap.parse("{\"name\":\"Hebbe\"}");
        assertThat(parsed.get(StandardName.name("name")), is(MessageValue.text("Hebbe")));
    }

    @Test
    public void parseList() throws Exception {
        final Map<? extends Name, ? extends Message.Value> parsed = JsonStringToMap.parse("[{\"name\":\"Hebbe\"}]");
        Message.Value root = parsed.get(StandardName.name("root"));
        assertThat(root.getField().getFieldType(), is(Message.FieldType.Values));

        final Map<? extends Name, ? extends Message.Value> map = root.asValues().values().iterator().next().asMap();
        assertThat(map.get(StandardName.name("name")), is(MessageValue.text("Hebbe")));
    }


    @Test
    public void parseSimpleJsonMap() throws Exception {
        final Map<? extends Name, ? extends Message.Value> parsed = JsonStringToMap.parse(simpleJson);

        //assertThat(parsed.get(StandardName.name("name")), is(MessageValue.text("Hebbe")));
    }

    @Test
    public void parse() throws Exception {
        final Map<? extends Name, ? extends Message.Value> parse = JsonStringToMap.parse(create_domain_json);

        System.out.println(Util.printValuesToString( (Iterable<Message.Value>) parse.values() ));
    }


    @Test
    public void testJsonTokenizer() {
        JsonStringToMap.JsonTokenizer jsonTokenizer = new JsonStringToMap.JsonTokenizerImpl("{\"name\":\"Hebbe\"}");

        final Optional<JsonStringToMap.JsonToken> startBrackets = jsonTokenizer.next();

        assertThat(startBrackets.isPresent(), is(true));
        assertThat(startBrackets.get().getText(), is("{"));
        assertThat(startBrackets.get().getType(), is(JsonStringToMap.JsonTokenType.JSON_START));

        final Optional<JsonStringToMap.JsonToken> key = jsonTokenizer.next();

        assertThat(key.isPresent(), is(true));
        assertThat(key.get().getText(), is("name"));
        assertThat(key.get().getType(), is(JsonStringToMap.JsonTokenType.KEY));

        final Optional<JsonStringToMap.JsonToken> value = jsonTokenizer.next();

        assertThat(value.isPresent(), is(true));
        assertThat(value.get().getText(), is("Hebbe"));
        assertThat(value.get().getType(), is(JsonStringToMap.JsonTokenType.VALUE));

        final Optional<JsonStringToMap.JsonToken> endBrackets = jsonTokenizer.next();

        assertThat(endBrackets.isPresent(), is(true));
        assertThat(endBrackets.get().getText(), is("}"));
        assertThat(endBrackets.get().getType(), is(JsonStringToMap.JsonTokenType.JSON_END));

        final Optional<JsonStringToMap.JsonToken> end = jsonTokenizer.next();

        assertThat(end.isPresent(), is(false));

    }
}