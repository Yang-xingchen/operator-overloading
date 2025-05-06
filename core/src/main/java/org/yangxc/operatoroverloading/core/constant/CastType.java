package org.yangxc.operatoroverloading.core.constant;

public enum CastType {

    /**
     * (newType)var
     */
    CAST,
    /**
     * new newType(var)
     */
    NEW,
    /**
     * var.{methodName}()
     */
    METHOD,
    /**
     * {Class.methodName}(var)
     */
    STATIC_METHOD,

}
