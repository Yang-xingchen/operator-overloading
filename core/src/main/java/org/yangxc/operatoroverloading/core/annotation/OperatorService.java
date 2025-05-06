package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * 导入，可在表达式中使用简单名称进行强转
     */
    Class<?>[] imports() default {};

    /**
     * 注释
     */
    DocType doc() default DocType.DOC;

}
