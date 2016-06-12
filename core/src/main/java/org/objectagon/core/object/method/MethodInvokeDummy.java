package org.objectagon.core.object.method;

import org.objectagon.core.object.Method;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodInvokeDummy implements Method.Invoke {

    @Override
    public void invoke(Method.InvokeWorker invokeWorker) {
        /* Code inside method should be used as method code */

        System.out.println("MethodInvokeDummy.invoke "+invokeWorker.getClass().getName() );
        invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber());
    }
}
