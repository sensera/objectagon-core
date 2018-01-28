package org.objectagon.core.developer.examples;

import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.method.ParamNameImpl;

public class PersonMetaTest {

    private Method.ParamName sumValue;
    private Method.ParamName addValue;

    @Before
    public void setUp() throws Exception {
        sumValue = ParamNameImpl.create("sumValue");
        addValue = ParamNameImpl.create("addValue");
    }

    @Test public void test() {
        //Given
/*
        PersonMeta personMeta = new PersonMeta();
        final TestMethodInvokeWorker invokeWorker = new TestMethodInvokeWorker(
                Stream.of(
                        InvokeParamImpl.create(sumValue, NamedField.number("number")),
                        InvokeParamImpl.create(addValue, NamedField.number("number"))),
                Stream.of(
                        NameValue.create(sumValue, MessageValue.number(100L)),
                        NameValue.create(addValue, MessageValue.number(100L)))
        );

        //When
        personMeta.sumValue(invokeWorker);

        //Then
        final KeyValue<Method.ParamName, Message.Value> paramNameValueKeyValue = invokeWorker.replyParams.get(0);

        assertEquals(sumValue, paramNameValueKeyValue.getKey());
        assertEquals((Long) 200L, paramNameValueKeyValue.getValue().asNumber());
*/
    }
}