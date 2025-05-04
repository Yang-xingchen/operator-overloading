package org.yangxc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OperatorFunction {

    /**
     * <pre>
     * 表达式
     * 可使用方法入参或者{@link #statements()}定义的变量，变量名称类型需符合操作
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

}
