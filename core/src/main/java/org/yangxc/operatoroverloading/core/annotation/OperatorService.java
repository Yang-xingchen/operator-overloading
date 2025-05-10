package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 定义处理的接口，通过该类定义生成对应操作代码
 * 该类必须为接口，且所有方法添加{@link ServiceFunction}或{@link ServiceField}注解
 * </pre>
 *
 * @see ServiceFunction
 * @see ServiceField
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface OperatorService {

    /**
     * 名称，默认为接口名+Impl
     */
    String value() default "";

    /**
     * 数字类型
     */
    NumberType numberType() default NumberType.INHERIT;

    /**
     * 导入
     * 可在表达式中使用简单名称进行强转
     * 可在表达式中使用简单名称+变量名称获取静态变量
     *
     * @see Cast 强转
     * @see OperatorClassConst 静态变量
     */
    Class<?>[] imports() default {};

    /**
     * 注释
     */
    DocType doc() default DocType.DOC;

}
