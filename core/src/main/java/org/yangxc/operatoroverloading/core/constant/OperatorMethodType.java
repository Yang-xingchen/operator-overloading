package org.yangxc.operatoroverloading.core.constant;

public enum OperatorMethodType {

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
