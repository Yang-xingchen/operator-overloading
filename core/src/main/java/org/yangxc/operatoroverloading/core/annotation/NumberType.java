package org.yangxc.operatoroverloading.core.annotation;

public enum NumberType {

    /**
     * 继承上级，顶级为{@link #BIG_DECIMAL}
     */
    INHERIT,
    /**
     * {@link java.math.BigDecimal}
     */
    BIG_DECIMAL,
    /**
     * {@link java.math.BigInteger}
     */
    BIG_INTEGER,
    /**
     * 原始类型
     * {@link double}: 包含小数或使用科学计数法定义或值处于{@link long}范围外
     * {@link long}: 不满足{@link double}条件且值处于{@link int}范围外
     * {@link int}: 范围内表示的整数
     */
    PRIMITIVE,

}
