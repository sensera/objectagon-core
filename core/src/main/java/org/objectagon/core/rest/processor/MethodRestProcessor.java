package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class MethodRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new MethodRestProcessor(((methodProtocolSend, testUser, request, response) -> {
                    final String code = new String(request.getContent());
                    System.out.println("MethodRestProcessor.attachToLocator ---------------------------------------------------------------------------");
                    System.out.println(code);
                    System.out.println("MethodRestProcessor.attachToLocator ---------------------------------------------------------------------------");
                    return methodProtocolSend.setCode(code);
                }))
        ).add("method").addIdentity("methodId").add("code").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new MethodRestProcessor(((methodProtocolSend, testUser, request, response) -> methodProtocolSend.getCode()))
        ).add("method").addIdentity("methodId").add("code").setOperation(Operation.Get);

        locatorBuilder.patternBuilder(
                new MethodRestProcessor(((methodProtocolSend, testUser, request, response) -> {
                    final Method.ParamName paramName = ParamNameImpl.create(request.getValue(Method.PARAM_NAME).text());
                    final Message.Field field = getFieldFromType(paramName.toString(), request.getValue(Method.PARAM_FIELD).text());
                    return methodProtocolSend.addParam(paramName, field,
                            request.getValueOptional(Method.DEFAULT_VALUE)
                                    .map(requestValue -> requestValue.value())
                                    .orElse(MessageValue.empty())
                    );
                }))
        ).add("method").addIdentity("methodId").add("param").setOperation(Operation.SaveNew);
    }

    MethodProtocolSendConsumer methodProtocolSendConsumer;

    public MethodRestProcessor(MethodProtocolSendConsumer methodProtocolSendConsumer) {
        this.methodProtocolSendConsumer = methodProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Optional<PathItem> pathItem = request.getPathItem(1);
        if (!pathItem.isPresent())
            throw new RuntimeException("Field ID not present!");
        Method.MethodIdentity methodIdentity = pathItem.get().address(Method.METHOD_IDENTITY);
        return methodProtocolSendConsumer.consume(testUser.createMethodProtocolSend(methodIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface MethodProtocolSendConsumer {
        Task consume(MethodProtocol.Send methodProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }

    private static Task.SuccessAction createCatchAndStoreAddressToAlias(ServerCore.TestUser testUser, Request request) {
        return (messageName, values) -> {
            MessageValueFieldUtil.create(request.queryAsValues())
                    .getValueByFieldOption(NamedField.text("alias"))
                    .ifPresent(value -> {
                        Message.Value address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                        testUser.setValue(NamedField.text(value.asText()), address);
                    });
        };
    }

    private static Message.Field getFieldFromType(String name, String typeName) {
        return NamedField.text(name);
    }

}



