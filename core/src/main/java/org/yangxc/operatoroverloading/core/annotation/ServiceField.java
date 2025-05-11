package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 定义service类的实例字段
 * 通过setter方法定义
 * 方法名称随意
 * 返回值必须为void或该接口类型(返回`this`)
 * 参数至少有一个。类型为该字段类型，名称为该字段名称(也可通过该注解定义名称，此时列表数量和参数数量需匹配)
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ServiceField {

    /**
     * 字段名称列表，需为空列表(使用方法参数名称)或与方法参数数量相同，名称符合java规范
     */
    String[] value() default {};

}
