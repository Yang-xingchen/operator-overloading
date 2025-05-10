package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 标识获取的静态字段
 *
 * 该字段必须为公共的静态字段，且所属的类需增加{@link OperatorClass}或{@link OperatorService}注解
 *
 * 使用方式
 * 1. 该类的全限定名称+'.'+字段名称
 * 2. 导入该类后{@link OperatorService#imports()}，可通过该类的简单名称+'.'+字段名称
 * </pre>
 *
 * @see OperatorClass
 * @see OperatorService
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface OperatorClassConst {
}
