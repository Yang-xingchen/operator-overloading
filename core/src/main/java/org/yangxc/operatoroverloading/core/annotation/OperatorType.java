package org.yangxc.operatoroverloading.core.annotation;

public enum OperatorType {

    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    REMAINDER("%"),
    ;

    public final String symbol;

    OperatorType(String symbol) {
        this.symbol = symbol;
    }

}
