package org.yangxc.operatoroverloading.core.annotation;

public enum Operator {

    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    REMAINDER("%"),
    ;

    public final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

}
