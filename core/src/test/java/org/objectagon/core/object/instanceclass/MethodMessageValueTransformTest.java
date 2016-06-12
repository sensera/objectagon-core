package org.objectagon.core.object.instanceclass;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.utils.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by christian on 2016-06-11.
 */
public class MethodMessageValueTransformTest {

    private MethodMessageValueTransform methodMessageValueTransform;
    private Method.ParamName paramName1;
    private Method.ParamName paramName2;
    private Message.Value textValue;
    private Message.Value numberValue;

    @Before
    public void setup() {
        methodMessageValueTransform = new MethodMessageValueTransform();
        paramName1 = ParamNameImpl.create("Name1");
        paramName2 = ParamNameImpl.create("Name2");
        textValue = MessageValue.text(NamedField.text("Text"), "Hubba");
        numberValue = MessageValue.number(NamedField.number("Number"), 10L);
    }

    @Test
    public void transformValuesListToValue() {
        List<KeyValue<Method.ParamName, Message.Value>> list = new ArrayList<>();

        list.add(MethodMessageValueTransform.createKeyValue(paramName1, textValue));
        list.add(MethodMessageValueTransform.createKeyValue(paramName2, numberValue));

        final Message.Value value = methodMessageValueTransform.createValuesTransformer().transform(list);

        final List<Message.Value> valuesList = MessageValueFieldUtil.create(value.asValues()).stream().collect(Collectors.toList());
        assertThat(valuesList.size(), is(2));

        List<KeyValue<Method.ParamName, Message.Value>> list2 = methodMessageValueTransform.createValuesTransformer().transform(value);

        assertThat(list2.size(), is(2));

        assertThat(list2.get(0).getKey(), is(paramName1));

        assertEquals(list2.get(0).getValue().asText(), textValue.asText());

        assertThat(list2.get(1).getKey(), is(paramName2));

        assertThat(list2.get(1).getValue().asNumber(), is(equalTo(numberValue.asNumber())));
    }

    @Test
    public void transformValuesValueToList() {
        List<Message.Value> valueList = new ArrayList<>();

        List<Message.Value> fieldValue1 = new ArrayList<>();
        fieldValue1.add(MessageValue.name(Method.PARAM_NAME, paramName1));
        fieldValue1.add(textValue);
        valueList.add(MessageValue.values(InstanceClassProtocol.KEY_VALUES, fieldValue1));

        List<Message.Value> fieldValue2 = new ArrayList<>();
        fieldValue2.add(MessageValue.name(Method.PARAM_NAME, paramName2));
        fieldValue2.add(numberValue);
        valueList.add(MessageValue.values(InstanceClassProtocol.KEY_VALUES, fieldValue2));

        Message.Value value = MessageValue.values(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS, valueList);
        List<KeyValue<Method.ParamName, Message.Value>> list = methodMessageValueTransform.createValuesTransformer().transform(value);

        assertThat(list.size(), is(2));
    }
}