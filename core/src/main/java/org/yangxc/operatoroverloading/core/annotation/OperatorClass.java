package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 定义处理的类，该类仅读取数据
 * </pre>
 *
 * @see Cast
 * @see Operator
 * @see OperatorClassConst
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface OperatorClass {
}
