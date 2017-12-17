package org.objectagon.core.object.method;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instanceclass.MethodMessageValueTransform;
import org.objectagon.core.object.method.data.MethodDataImpl;
import org.objectagon.core.object.utils.ObjectagonCompiler;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodImpl extends EntityImpl<Method.MethodIdentity, Method.MethodData, StandardVersion, MethodImpl.MethodWorker> implements Method {

    static final MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

    private ObjectagonCompiler<Invoke> objectagonCompiler;

    private Map<StandardVersion,Invoke> compiledMethods = new HashMap<>();

    public MethodImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Method.DATA_TYPE);

    }

    public synchronized ObjectagonCompiler<Invoke> getObjectagonCompiler() {
        if (objectagonCompiler==null) {
            objectagonCompiler = createStandardCompiler(); //TODO better initialization
        }
        return objectagonCompiler;
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected MethodData createNewData(Optional<StandardVersion> version) {
        return MethodDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)));
    }

    @Override
    protected MethodWorker createWorker(WorkerContext workerContext) {
        return new MethodWorker(workerContext);
    }

    public class MethodWorker extends EntityWorkerImpl {

        public MethodWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public InstanceClassProtocol.Send createInstanceClassProtocolSend(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,InstanceClassProtocol>createReceiver(
                            InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL)
                    .createSend(() -> getWorkerContext().createSend(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL, instanceClassIdentity));
        }

        public InstanceProtocol.Send createInstanceProtocolSend(Instance.InstanceIdentity instanceIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,InstanceProtocol>createReceiver(
                            InstanceProtocol.INSTANCE_PROTOCOL)
                    .createSend(() -> getWorkerContext().createSend(InstanceProtocol.INSTANCE_PROTOCOL, instanceIdentity));
        }
    }

    @Override
    protected MethodData upgrade(MethodData data, DataRevision<MethodIdentity, StandardVersion> newDataRevision, Transaction transaction) {
        return data; // TODO fix this
    }

    @Override
    protected MethodIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        Method.ConfigMethod configMethod = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return FindNamedConfiguration.finder(configurations)
                .createConfiguredAddress((serverId, timestamp, addressId) ->
                    new MethodIdentityImpl(serverId, timestamp, addressId, configMethod.getMetaIdentity()));
    }

    @Override
    protected void handle(MethodWorker worker) {
        triggerBuilder(worker)
                .trigger(MethodProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(MethodProtocol.MessageName.SET_NAME, write(this::<ChangeMethod>setName))
                .trigger(MethodProtocol.MessageName.GET_CODE, read(this::getCode))
                .trigger(MethodProtocol.MessageName.SET_CODE, write(this::<ChangeMethod>setCode))
                .trigger(MethodProtocol.MessageName.INVOKE, read(this::invoke))
                .trigger(MethodProtocol.MessageName.ADD_PARAM, write(this::<ChangeMethod>addParam))
                .trigger(MethodProtocol.MessageName.REMOVE_PARAM, write(this::<ChangeMethod>removeParam))
                .orElse(w -> super.handle(w));
    }

    private void addParam(MethodWorker methodWorker, MethodData methodData, ChangeMethod changeMethod, Message.Values preparedValues) {
        ParamName paramName = methodWorker.getValue(Method.PARAM_NAME).asName();
        Message.Field field = methodWorker.getValue(Method.PARAM_FIELD).asField();
        Message.Value defaultValue = methodWorker.getValue(Method.DEFAULT_VALUE);
        if (defaultValue.isUnknown())
            changeMethod.addInvokeParam(paramName, field);
        else
            changeMethod.addInvokeParam(paramName, field, defaultValue);
    }

    private void removeParam(MethodWorker methodWorker, MethodData methodData, ChangeMethod changeMethod, Message.Values preparedValues) {
        ParamName paramName = methodWorker.getValue(Method.PARAM_NAME).asName();
        changeMethod.removeInvokeParam(paramName);
    }

    private void getName(MethodWorker methodWorker, MethodData methodData) {
        methodWorker.replyWithParam(MessageValue.name(Method.METHOD_NAME, methodData.getName()));
    }

    private void setName(MethodWorker methodWorker, MethodData methodData, ChangeMethod changeMethod, Message.Values preparedValues) {
        Method.MethodName methodName = methodWorker.getValue(Method.METHOD_NAME).asName();
        changeMethod.setName(methodName);
    }

    private void getCode(MethodWorker methodWorker, MethodData methodData) {
        methodWorker.replyWithParam(MessageValue.text(Method.CODE, methodData.getCode()));
    }

    private void setCode(MethodWorker methodWorker, MethodData methodData, ChangeMethod changeMethod, Message.Values preparedValues) {
        String code = methodWorker.getValue(Method.CODE).asText();
        changeMethod.setCode(code);
        //System.out.println("MethodImpl.setCode "+code);
    }

    private void invoke(MethodWorker methodWorker, MethodData methodData) {
        final List<KeyValue<ParamName, Message.Value>> paramNameValueList = methodMessageValueTransform.createValuesTransformer().transform(methodWorker.getValue(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS));
        //System.out.println("MethodImpl.invoke with "+paramNameValueList.size()+" params");
        //paramNameValueList.stream().forEach(paramNameValueKeyValue -> System.out.println("MethodImpl.invoke "+paramNameValueKeyValue.getKey()+"="+paramNameValueKeyValue.getValue().asText()));
        Invoke invoke = compiledMethods.get(methodData.getVersion());
        if (invoke==null) {
            try {
                final Class<Invoke> compile = getObjectagonCompiler().compile(methodData.getCode());
                invoke = compile.newInstance();
                compiledMethods.put(methodData.getVersion(), invoke);
            } catch (Exception e) {
                methodWorker.failed(ErrorClass.METHOD, ErrorKind.FAILED_TO_INVOKE_METHOD, Arrays.asList(MessageValue.text(e.getMessage())));
                return;
            }
        }
        final List<KeyValue<ParamName, Message.Value>> replyParams = new ArrayList<>();
        invoke.invoke(new InvokeWorker() {
            @Override
            public List<ParamName> getInvokeParams() {
                return methodData.getInvokeParams().stream().map(InvokeParam::getName).collect(Collectors.toList());
            }

            @Override
            public Message.Value getValue(String name) {
                return getValue(new ParamNameImpl(name));
            }

            @Override
            public void replyOk() {
                methodWorker.replyOk();
            }

            @Override
            public Message.Value getValue(ParamName paramName) {
                final Message.Value value1 = methodData.getInvokeParams().stream()
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

            @Override
            public void replyOkWithParams(Message.Value... values) {
                methodWorker.replyWithParam(StandardProtocol.MessageName.OK_MESSAGE, Arrays.asList(values));
            }

            @Override
            public void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values) {
                methodWorker.failed(errorClass, errorKind, Arrays.asList(values));
            }

            @Override
            public <T> ValueCreator<T> setValue(String paramName) {
                return setValue(new ParamNameImpl(paramName));
            }

            @Override
            public <T> ValueCreator<T> setValue(ParamName paramName) {
                return value -> {
                    replyParams.add(methodMessageValueTransform.createKeyValue(paramName,
                                        getValue(paramName).getField().createValueFromUnknown(value)));
                };
            }

            @Override
            public Task createInstance(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
                return methodWorker.createInstanceClassProtocolSend(instanceClassIdentity)
                        .createInstance()
                        .addFailedAction(methodWorker::failed);
            }

            @Override
            public Task createInstanceAndAddToRelation(InstanceClass.InstanceClassIdentity instanceClassIdentity, RelationClass.RelationClassIdentity relationClassIdentity) {
                return methodWorker.createInstanceClassProtocolSend(instanceClassIdentity)
                        .createInstance()
                        .addSuccessAction((messageName, values) -> {
                            Instance.InstanceIdentity newInstance = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();

                            methodWorker.createInstanceProtocolSend(newInstance).addRelation(
                                    relationClassIdentity,
                                    methodWorker.getValue(Instance.INSTANCE_IDENTITY).asAddress());
                        })
                        .addFailedAction(methodWorker::failed);
            }
        });
        if (replyParams.isEmpty())
            methodWorker.replyOk();
        else
            methodWorker.replyWithParam(MessageValue.values(Method.PARAMS, methodMessageValueTransform.createValuesTransformer().transform(replyParams)));
    }

    private static ObjectagonCompiler<Invoke> createStandardCompiler() {
        System.out.println("MethodImpl.createStandardCompiler "+new File(".").getAbsolutePath()+" **************************************************************************************************** ");
        File compilePath = new File("/tmp/objectagoncompiler/");
        if (!compilePath.exists() && !compilePath.mkdirs() )
            throw new RuntimeException("Create dirs '"+compilePath.getAbsolutePath()+"' failed!");
        String classpath = "./target/classes:./core/target/classes:./object/target/classes";
/*
        if (!new File(classpath).exists())
            classpath = "./target/classes";
*/
            //throw new RuntimeException("classpath '" + classpath + " does not exist!");
        ObjectagonCompiler<Invoke> objectagonCompiler = new ObjectagonCompiler<>(
                compilePath,
                classpath,
                "org.objectagon.core.compiledmethod",
                Arrays.asList("org.objectagon.core.object.Method"));
        return objectagonCompiler;
    }
}
