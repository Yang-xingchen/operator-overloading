package org.yangxc.operatoroverloading.core.annotation;

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

    /**
     * 是否处理{@link #exp()}, 设置为false将exp直接写入(需自行处理语法, 程序只添加变量定义和结尾`;`)
     */
    boolean pares() default true;

}
