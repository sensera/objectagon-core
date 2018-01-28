package org.objectagon.core.developer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ObjectagonClass {

    String name() default "";
    String alias() default "";
    Class[] meta() default {};
    boolean autoCreateMetaMethods() default true;

}
