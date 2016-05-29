package org.objectagon.core.object.method;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.object.method.data.MethodDataImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.ObjectagonCompiler;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodImpl extends EntityImpl<Method.MethodIdentity, Method.MethodData, StandardVersion, MethodImpl.MethodWorker> implements Method {

    private ObjectagonCompiler<Method.Invoke> objectagonCompiler;

    private Map<StandardVersion,Method.Invoke> compiledMethods = new HashMap<>();

    public MethodImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Method.DATA_TYPE);
        objectagonCompiler = createStandardCompiler(); //TODO better initialization
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

    }

    @Override
    protected MethodData upgrade(MethodData data, DataVersion<MethodIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data; // TODO fix this
    }

    @Override
    protected MethodIdentity createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(MethodIdentityImpl::new);
    }

    @Override
    protected void handle(MethodWorker worker) {
        triggerBuilder(worker)
                .trigger(MethodProtocol.MessageName.GET_CODE, read(this::getCode))
                .trigger(MethodProtocol.MessageName.SET_CODE, write(this::<ChangeMethod>setCode))
                .trigger(MethodProtocol.MessageName.INVOKE, read(this::invoke))
/*
                .trigger(InstanceClassProtocol.MessageName.GET_RELATIONS, read(this::getRelations))
                .trigger(InstanceClassProtocol.MessageName.ADD_FIELD, write(this::<ChangeInstanceClass>addField, this::createField))
                .trigger(InstanceClassProtocol.MessageName.ADD_RELATION, write(this::<ChangeInstanceClass>setRelationClass, this::createRelationClass))
                .trigger(InstanceClassProtocol.MessageName.SET_RELATION, write(this::<ChangeInstanceClass>setRelationClass))
                .trigger(InstanceClassProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(InstanceClassProtocol.MessageName.SET_NAME, write(this::<ChangeInstanceClass>setName))
                .trigger(InstanceClassProtocol.MessageName.CREATE_INSTANCE, this::createInstance)
                .trigger(InstanceClassProtocol.MessageName.GET_INSTANCE_BY_ALIAS, read(this::getInstanceByAlias))
                .trigger(InstanceClassProtocol.MessageName.ADD_INSTANCE_ALIAS, write(this::<ChangeInstanceClass>addInstanceAlias))
                .trigger(InstanceClassProtocol.MessageName.REMOVE_INSTANCE_ALIAS, write(this::<ChangeInstanceClass>removeInstanceAlias))
*/
                .orElse(w -> super.handle(w));
    }

    private void getCode(MethodWorker methodWorker, MethodData methodData) {
        methodWorker.replyWithParam(MessageValue.text(Method.CODE, methodData.getCode()));
    }

    private void setCode(MethodWorker methodWorker, MethodData methodData, ChangeMethod changeMethod, Message.Values preparedValues) {
        String code = methodWorker.getValue(Method.CODE).asText();
        changeMethod.setCode(code);
        System.out.println("MethodImpl.setCode "+code);
    }

    private void invoke(MethodWorker methodWorker, MethodData methodData) {
        methodData.getCode();
        Invoke invoke = compiledMethods.get(methodData.getVersion());
        if (invoke==null) {
            try {
                final Class<Invoke> compile = objectagonCompiler.compile(methodData.getCode());
                invoke = compile.newInstance();
                compiledMethods.put(methodData.getVersion(), invoke);
            } catch (Exception e) {
                methodWorker.failed(ErrorClass.METHOD, ErrorKind.FAILED_TO_INVOKE_METHOD, Arrays.asList(MessageValue.text(e.getMessage())));
            }
        }
        invoke.invoke(new InvokeWorker() {
            @Override
            public void replyOk() {
                methodWorker.replyOk();
            }

            @Override
            public Message.Value getValue(Message.Field field) {
                return methodWorker.getValue(field);
            }

            @Override
            public void replyOkWithParams(Message.Value... values) {
                methodWorker.replyWithParam(StandardProtocol.MessageName.OK_MESSAGE, Arrays.asList(values));
            }

            @Override
            public void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values) {
                methodWorker.failed(errorClass, errorKind, Arrays.asList(values));
            }
        });

        // Compile and invoke
    }

    private static ObjectagonCompiler<Method.Invoke> createStandardCompiler() {
        File compilePath = new File("/tmp/objectagoncompiler/");
        if (!compilePath.exists() && !compilePath.mkdirs() )
            throw new RuntimeException("Create dirs '"+compilePath.getAbsolutePath()+"' failed!");
        ObjectagonCompiler<Method.Invoke> objectagonCompiler = new ObjectagonCompiler<>(
                compilePath,
                "/projects/objectagon/objectagon-core/core/target/core-1.0-SNAPSHOT.jar",
                "org.objectagon.core.compiledmethod",
                Arrays.asList("org.objectagon.core.object.Method"));
        return objectagonCompiler;
    }
}
