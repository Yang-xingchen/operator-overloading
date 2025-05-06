package org.yangxc.operatoroverloading.core.constant;

public enum OperatorType {

    /**
     * a+b
     */
    PRIMITIVE,
    /**
     * a.add(b)
     */
    METHOD,
    /**
     * Class.add(a,b)
     */
    STATIC_METHOD,

}
