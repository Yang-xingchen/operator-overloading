package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 定义重载的方法
 *
 * 方法所属的类需增加{@link OperatorClass}注解
 *
 * 可定义在以下方法(`T`为操作的类，`a`、`b`为操作的实例)
 * 1. 实例方法: 需要为`T xxx(T b)`格式，`a+b`编译为`a.xxx(b)`
 * 2. 静态方法: 需要为`T xxx(T a, T b)`格式，`a+b`编译为`xxx(a, b)`
 *
 * </pre>
 *
 * @see OperatorClass
 * @see OperatorType
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Operator {

    /**
     * 重载的运算符
     */
    OperatorType value();

}
