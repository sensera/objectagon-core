package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.utils.ReadStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2017-04-17.
 */
public class MapToBatchUpdateTest {

    String create_person_method_code = "invokeWorker.setValue(\"sumValue\").set(invokeWorker.getValue(\"sumValue\").asNumber() + invokeWorker.getValue(\"addValue\").asNumber());";

    String create_domain_json_old = "[\n" +
            "    {\"main.meta\": {\n" +
            "        \"type\": \"meta\",\n" +
            "                \"methods\": [\n" +
            "        {\"create.person.method\": {\n" +
            "            \"code\": create_person_method_code\n" +
            "        }}\n" +
            "        ]\n" +
            "    }},\n" +
            "    {\"main.class\": {\n" +
            "        \"type\": \"class\",\n" +
            "                \"relations\": [\n" +
            "        {\"main.person.relation\": {\n" +
            "            \"targetClass\": \"person.class\"\n" +
            "        }}\n" +
            "        ]\n" +
            "    }},\n" +
            "    {\"person.class\": {\n" +
            "        \"type\": \"class\",\n" +
            "                \"fields\": [\n" +
            "        {\"Name\": {\n" +
            "            \"type\": \"Text\"\n" +
            "        }},\n" +
            "        {\"Age\": {\n" +
            "            \"type\": \"Number\"\n" +
            "        }},\n" +
            "        {\"Comment\": {\n" +
            "            \"type\": \"Text\"\n" +
            "        }}\n" +
            "        ]\n" +
            "    }}\n" +
            "    ]";

    String create_domain_json = "dfsafa";

    String create_domain_json2 =
            "[" +
            "     {\"main.meta\": {" +
            "       \"type\": \"meta\"," +
            "       \"methods\": [" +
            "             {\"create.person.method\": {" +
            "                 \"code\": \"create_person_method_code\"" +
            "             }}" +
            "         ]     " +
            "     }}," +
            "     {\"main.class\": {" +
            "         \"type\": \"class\"," +
            "         \"relations\": [" +
            "             {\"main.person.relation\": {" +
            "                 \"targetClass\": \"person.class\"" +
            "             }}" +
            "         ]" +
            "     }}," +
            "     {\"person.class\": {" +
            "         \"type\": \"class\"," +
            "         \"fields\": [" +
            "             {\"Name\": {" +
            "                 \"type\": \"Text\"" +
            "             }}," +
            "             {\"Age\": {" +
            "                 \"type\": \"Number\"" +
            "             }}," +
            "             {\"Comment\": {" +
            "                 \"type\": \"Text\"" +
            "             }}" +
            "         ]" +
            "     }}" +
            " ]";

    private BatchUpdate.AddBasis construct;

    @Before
    public void setup() throws IOException {
        construct = mock(BatchUpdate.AddBasis.class);
        create_domain_json = new String(ReadStream.readStream(getClass().getClassLoader().getResourceAsStream("create_domain.json")));
    }

    @Test
    @Ignore
    public void transferMeta() throws Exception {
        Message.Value meta = MessageValue.map(createMetaMap("main.meta", "addValue"));

        Message.Values updateCommandField = MessageValue.values(meta).asValues();

        MapToBatchUpdate.transfer(updateCommandField).accept(construct);

        verify(construct, atMost(0)).addClass(any(BatchUpdate.ClassBasis.class));
        verify(construct, times(1)).addMeta(any(BatchUpdate.MetaBasis.class));
    }

    @Test
    @Ignore
    public void transferClass() throws Exception {
        Message.Value meta = MessageValue.map(createClassMap("main.class", "field1", "field2", "field3"));

        Message.Values updateCommandField = MessageValue.values(meta).asValues();

        MapToBatchUpdate.transfer(updateCommandField).accept(construct);

        verify(construct, atMost(0)).addMeta(any(BatchUpdate.MetaBasis.class));
        verify(construct, times(1)).addClass(any(BatchUpdate.ClassBasis.class));
    }

    // ------------------ Util methods ----------------------

    private Map<Name, Message.Value> createMetaMap(String name, String methodName) {
        Map<Name,Message.Value> map = new HashMap<>();

        map.put(MapToBatchUpdate.NAME, MessageValue.name(StandardName.name(name)));
        map.put(MapToBatchUpdate.TYPE, MessageValue.name(MapToBatchUpdate.META));
        map.put(MapToBatchUpdate.METHODS, MessageValue.values(MessageValue.map(createMethodMap(methodName))));

        return map;
    }

    private Map<Name, Message.Value> createMethodMap(String methodName) {
        Map<Name,Message.Value> method1 = new HashMap<>();

        method1.put(MapToBatchUpdate.NAME, MessageValue.name(StandardName.name(methodName)));
        method1.put(MapToBatchUpdate.CODE, MessageValue.text("code"));
        return method1;
    }

    private Map<Name, Message.Value> createClassMap(String name, String field1, String field2, String field3) {
        Map<Name,Message.Value> map = new HashMap<>();

        map.put(MapToBatchUpdate.NAME, MessageValue.name(StandardName.name(name)));
        map.put(MapToBatchUpdate.TYPE, MessageValue.name(MapToBatchUpdate.CLASS));
        map.put(MapToBatchUpdate.FIELDS, MessageValue.values(
                MessageValue.map(createFieldMap(field1)),
                MessageValue.map(createFieldMap(field2)),
                MessageValue.map(createFieldMap(field3))
        ));

        return map;
    };

    private Map<Name, Message.Value> createFieldMap(String fieldName) {
        Map<Name,Message.Value> method1 = new HashMap<>();

        method1.put(MapToBatchUpdate.NAME, MessageValue.name(StandardName.name(fieldName)));
        method1.put(MapToBatchUpdate.TYPE, MessageValue.text("number"));
        return method1;
    }

}