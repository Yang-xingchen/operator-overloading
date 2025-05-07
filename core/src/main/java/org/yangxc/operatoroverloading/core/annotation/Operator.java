package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Operator {

    /**
     * 操作类型
     * 如果为实例方法，需要为`T add(T b)`格式，其中`T`为实例类型，`a+b`编译为`a.add(b)`
     * 如果为静态方法，需要为`T add(T a, T b)`格式，其中`T`为操作类型，`a+b`编译为`add(a, b)`
     */
    OperatorType value();

}
