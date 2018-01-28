package org.objectagon.core.developer;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ObjectagonMethodParams.class)
public @interface ObjectagonMethodParam {

    String name() default "";
    String alias() default "";
    String field() default "number";
    String defaultValue() default "0";

}
