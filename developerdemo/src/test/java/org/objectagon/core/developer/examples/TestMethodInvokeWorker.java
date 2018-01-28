package org.objectagon.core.developer.examples;

public class TestMethodInvokeWorker {}
/*
implements Method.InvokeWorker {
    static final MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

    final List<KeyValue<Method.ParamName, Message.Value>> replyParams = new ArrayList<>();
    final List<Method.InvokeParam> invokeParams;
    final List<KeyValue<Method.ParamName, Message.Value>> paramNameValueList;

    public TestMethodInvokeWorker(Stream<Method.InvokeParam> invokeParams, Stream<KeyValue<Method.ParamName, Message.Value>> paramNameValueList) {
        this.invokeParams = invokeParams.collect(Collectors.toList());
        this.paramNameValueList = paramNameValueList.collect(Collectors.toList());
    }

    @Override public List<Method.ParamName> getInvokeParams() {
        return invokeParams.stream().map(Method.InvokeParam::getName).collect(Collectors.toList());
    }

    public Message.Value getValue(String name) {
        return getValue(ParamNameImpl.create(name));
    }

    @Override public Message.Value getValue(Method.ParamName paramName) {
        final Message.Value value1 = invokeParams.stream()
                .filter(invokeParam -> invokeParam.getName().equals(paramName))
                .findAny()
                .map(invokeParam -> {
                    final Message.Value value = paramNameValueList.stream()
                            .filter(paramNameValueKeyValue -> {
                                return paramNameValueKeyValue.getKey().equals(invokeParam.getName());
                            })
                            .map(KeyValue::getValue)
                            .findAny()
                            .orElse(MessageValue.empty());
                    if (value.isUnknown())
                        return invokeParam.getDefaultValue().orElse(value);
                    return value;
                })
                .orElse(MessageValue.empty());
        System.out.println("MethodImpl.getValue["+paramName+"]="+value1.asText());
        return value1;
    }

    @Override public Task createInstance(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public Task createInstanceAndAddToRelation(InstanceClass.InstanceClassIdentity instanceClassIdentity, RelationClass.RelationClassIdentity relationClassIdentity) {
        throw new RuntimeException("Not implemented!");
    }

    @Override public void replyOk() {

    }

    @Override public void replyOkWithParams(Message.Value... values) {

    }

    @Override public void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values) {

    }

    @Override public <T> Method.ValueCreator<T> setValue(String paramName) {
        return setValue(ParamNameImpl.create(paramName));
    }

    @Override public <T> Method.ValueCreator<T> setValue(Method.ParamName paramName) {
        return value -> {
            replyParams.add(methodMessageValueTransform.createKeyValue(paramName,
                                                                       getValue(paramName).getField().createValueFromUnknown(value)));
        };
    }
}
*/
