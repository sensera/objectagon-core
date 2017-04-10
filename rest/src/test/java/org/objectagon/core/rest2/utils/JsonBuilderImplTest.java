package org.objectagon.core.rest2.utils;

import org.junit.Test;
import org.objectagon.core.msg.name.StandardName;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-04-10.
 */
public class JsonBuilderImplTest {

    @Test
    public void simple() {
        JsonBuilderImpl jsonBuilder = new JsonBuilderImpl();
        final JsonBuilder.Builder builder = jsonBuilder.builder();
        builder.addChild(StandardName.name("Name")).set("Gurra");

        String json = builder.build().toString();

        assertThat(json, equalTo("{\"Name\": \"Gurra\"}"));
    }

    @Test
    public void twoValues() {
        JsonBuilderImpl jsonBuilder = new JsonBuilderImpl();
        final JsonBuilder.Builder builder = jsonBuilder.builder();
        builder.addChild(StandardName.name("Name")).set("Gurra");
        builder.addChild(StandardName.name("Year")).set("2017");

        String json = builder.build().toString();

        assertThat(json, equalTo("{\"Year\": \"2017\", \"Name\": \"Gurra\"}"));
    }

    @Test
    public void list() {
        JsonBuilderImpl jsonBuilder = new JsonBuilderImpl();
        final JsonBuilder.Builder builder = jsonBuilder.builder();
        builder.addChild(StandardName.name("Name")).set("Gurra");
        builder.addChild(StandardName.name("Year")).set("2017");
        builder.addChild(StandardName.name("List")).setChildValue(StandardName.name("Urk"), "1");
        builder.addChild(StandardName.name("List")).setChildValue(StandardName.name("Urk2"), "1-1");
        builder.addChild(StandardName.name("List")).set("Murk");

        String json = builder.build().toString();

        assertThat(json, equalTo("{\"Year\": \"2017\", \"List\": [{\"Urk\": \"1\"}, {\"Urk2\": \"1-1\"}, \"Murk\"], \"Name\": \"Gurra\"}"));
    }

    @Test
    public void testFormat() {
        JsonBuilderImpl.JsonFormatter jsonFormatter = new JsonBuilderImpl.JsonFormatter(false);
        jsonFormatter.append("Gurra");

        String json = jsonFormatter.json.toString();

        assertThat(json, equalTo("Gurra"));
    }

    @Test
    public void testFormatValueSpaced() {
        JsonBuilderImpl.JsonFormatter jsonFormatter = new JsonBuilderImpl.JsonFormatter(true);
        jsonFormatter.startBrace();
        jsonFormatter.setKey(1,"Name");
        jsonFormatter.setValue("Gurra");
        jsonFormatter.endBrace(0);

        String json = jsonFormatter.json.toString();

        assertThat(json, equalTo("{\n    \"Name\": \"Gurra\"\n}\n"));
    }

    @Test
    public void testFormatValue() {
        JsonBuilderImpl.JsonFormatter jsonFormatter = new JsonBuilderImpl.JsonFormatter(false);
        jsonFormatter.startBrace();
        jsonFormatter.setKey(1,"Name");
        jsonFormatter.setValue("Gurra");
        jsonFormatter.endBrace(0);

        String json = jsonFormatter.json.toString();

        assertThat(json, equalTo("{\"Name\": \"Gurra\"}"));
    }

/*
    private void code() {
        new Method.Invoke() {
            @Override
            public void invoke(Method.InvokeWorker invokeWorker) {
                invokeWorker.createInstanceAndAddToRelation()
                invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber())
            }
        };

    }
*/

    //"{"errorClass": REST_SERVICE,
    // "errorKind": TARGET_IS_NULL,
    // "value": [
    //      org.objectagon.core.rest2.service.actions.AbstractSessionRestAction.lambda$createTask$1(AbstractSessionRestAction.java:43)
    //      java.util.Optional.orElseThrow(Optional.java:290)
    //      org.objectagon.core.rest2.service.actions.AbstractSessionRestAction.createTask(AbstractSessionRestAction.java:43)
    //      org.objectagon.core.rest2.service.RestService$RestServiceWorker.createTaskFromMethodAndPath(RestService.java:110)
    //      org.objectagon.core.rest2.service.RestService$ProcessRestRequestAction.internalRun(RestService.java:178)
    //      org.objectagon.core.rest2.service.RestService$ProcessRestRequestAction.internalRun(RestService.java:152)
    //      org.objectagon.core.msg.receiver.AsyncAction.run(AsyncAction.java:37)
    //      org.objectagon.core.msg.receiver.StandardReceiverImpl.lambda$handle$0(StandardReceiverImpl.java:35)
    //      org.objectagon.core.msg.receiver.ReactorImpl.react(ReactorImpl.java:28)
    //      org.objectagon.core.msg.receiver.StandardReceiverImpl.handle(StandardReceiverImpl.java:31)
    //      org.objectagon.core.msg.receiver.StandardReceiverImpl.handle(StandardReceiverImpl.java:9)
    //      org.objectagon.core.msg.receiver.BasicReceiverImpl.lambda$receive$0(BasicReceiverImpl.java:42)
    //      org.objectagon.core.msg.envelope.StandardEnvelope.unwrap(StandardEnvelope.java:33)
    //      org.objectagon.core.msg.receiver.BasicReceiverImpl.receive(BasicReceiverImpl.java:40)
    //      org.objectagon.core.msg.envelope.StandardEnvelope.targets(StandardEnvelope.java:78)
    //      org.objectagon.core.server.ServerImpl$EnvelopeProcessorImpl.run(ServerImpl.java:161)
    //      java.lang.Thread.run(Thread.java:745), User, REST_SERVICE, TARGET_IS_NULL]
    // }"

}