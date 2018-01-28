package org.objectagon.core.developer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectagonMethod {

    String name() default "";
    String alias() default "";

    Class[] meta() default {};

    /* name of method in meta */
    String methodName() default "";

    ObjectagonMethodParam[] params() default {};

    ObjectagonMethodFieldMapping[] fieldMappings() default  {};

}
