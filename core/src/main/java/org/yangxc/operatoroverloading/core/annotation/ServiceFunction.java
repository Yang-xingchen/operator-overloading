package org.yangxc.operatoroverloading.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 定义处理的方法，通过该类定义生成对应操作方法
 *
 * 方法所属的类需增加{@link OperatorService}注解
 * </pre>
 *
 * @see OperatorService
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ServiceFunction {

    /**
     * <pre>
     * 表达式
     * 可使用静态字段{@link OperatorClassConst}、实例字段{@link ServiceField}、方法入参、本地字段{@link #statements()}定义的变量，变量名称类型需符合操作
     * 可使用数字，会自动转换为{@link #numberType()}定义的类型
     * 数字定义同Java语法
     * 支持'+','-','*','/','%'运算及'()'使用子表达式
     * 支持使用 '(type)exp' 将 'exp' 转成 'type' 类型。如果 'exp' 为整个表达式结果，可自动获取返回类型进行转化
     * </pre>
     */
    String value();

    /**
     * 表达式, 定义本地变量
     */
    Statement[] statements() default {};

    /**
     * 数字类型
     */
    NumberType numberType() default NumberType.INHERIT;

    /**
     * 注释
     */
    DocType doc() default DocType.INHERIT;

    /**
     * 是否处理{@link #value()}, 设置为false将value直接写入(需自行处理语法，程序只添加`return`和结尾`;`)
     */
    boolean pares() default true;

}
