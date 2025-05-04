package org.yangxc.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Statement {

    Class<?> type();

    String varName();

    /**
     * 表达式，支持同{@link OperatorFunction#value()}
     */
    String exp();

    /**
     * 数字类型
     */
    NumberType numberType() default NumberType.INHERIT;

}
