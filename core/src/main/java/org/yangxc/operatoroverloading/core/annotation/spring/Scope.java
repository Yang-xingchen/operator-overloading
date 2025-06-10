package org.yangxc.operatoroverloading.core.annotation.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Scope {

    /**
     * 同{@link org.springframework.context.annotation.Scope}
     */
    String value() default "singleton";

}
