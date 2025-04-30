package org.yangxc.core.annotation;

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

}
