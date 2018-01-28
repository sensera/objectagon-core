package org.objectagon.core.developer.examples;

import org.objectagon.core.developer.*;
import org.objectagon.core.object.Method;

@ObjectagonMeta
public class PersonMeta {

    @ObjectagonMetaMethod(
            params = {
                    @ObjectagonMethodParam(name = "sumValue", alias = "sumValue"),
                    @ObjectagonMethodParam(name = "addValue", alias = "addValue")
            }
    )

    void sumValue(Method.InvokeWorker invokeWorker) {
        invokeWorker
                .setValue("sumValue")
                .set(
                        invokeWorker.getValue("sumValue").asNumber()
                                + invokeWorker.getValue("addValue").asNumber()
                    );
    }

}

