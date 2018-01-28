package org.objectagon.core.developer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectagonMetaMethod {

    String name() default "";
    String alias() default "";

    ObjectagonMethodParam[] params() default {};
}


/*{
  "metas":[{
    "name":"meta",
    "alias":"meta",
    "methods":[{
      "name":"Hubba",
      "alias":"Hello",
      "code":"invokeWorker.setValue(\"sumValue\").set(invokeWorker.getValue(\"sumValue\").asNumber() + invokeWorker.getValue(\"addValue\").asNumber());",
      "params":[
        {
          "name":"sumValue",
          "alias":"sumValue",
          "field":"number",
          "default":"0"
        },{
          "name":"addValue",
          "alias":"addValue",
          "field":"number",
          "default":"1"
        }
      ]
    }]
  }]
}
*/