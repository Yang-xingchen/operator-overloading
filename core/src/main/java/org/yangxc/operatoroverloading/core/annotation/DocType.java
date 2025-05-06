package org.yangxc.operatoroverloading.core.annotation;

public enum DocType {

    /**
     * 继承上级
     * 用于接口上为{@link #DOC}
     */
    INHERIT,
    /**
     * 无注释
     */
    NONE,
    /**
     * 仅javadoc
     */
    DOC,
    /**
     * 仅表达式
     * 用于接口上为{@link #NONE}
     */
    EXP,
    /**
     * javadoc+表达式
     * 用于接口上为{@link #DOC}
     */
    DOC_EXP

}
