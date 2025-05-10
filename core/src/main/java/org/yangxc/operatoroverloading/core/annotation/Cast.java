package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 标识可通过该方法/构造函数进行类型转换
 *
 * 方法所属的类需增加{@link OperatorClass}注解
 *
 * 可定义在以下方法(`T`为转换前的类，`R`为转换结果类，`t`为需转换的实例)
 * 1. 构造函数: 需要为`R(T t)`格式，`(R)t`编译为`new R(t)`
 * 2. 实例方法: 需要为`R xxx()`格式，`(R)t`编译为`t.xxx()`
 * 3. 静态方法: 需要为`R xxx(T t)`格式，`(R)t`编译为`xxx(t)`
 * </pre>
 *
 * @see OperatorClass
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Cast {
}
