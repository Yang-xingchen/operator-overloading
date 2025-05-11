package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.annotation.OperatorType;
import org.yangxc.operatoroverloading.core.constant.OperatorMethodType;

import java.util.Objects;

public final class OperatorOverloadingContext {

    private final OperatorType operator;
    private final OperatorMethodType type;
    private final String className;
    private final String name;
    private final String paramType;
    private final String resultType;

    public OperatorOverloadingContext(OperatorType operator, OperatorMethodType type, String className, String name, String paramType, String resultType) {
        this.operator = operator;
        this.type = type;
        this.className = className;
        this.name = name;
        this.paramType = paramType;
        this.resultType = resultType;
    }

    public OperatorType getOperator() {
        return operator;
    }

    public OperatorMethodType getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getParamType() {
        return paramType;
    }

    public String getResultType() {
        return resultType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (OperatorOverloadingContext) obj;
        return Objects.equals(this.operator, that.operator) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.paramType, that.paramType) &&
                Objects.equals(this.resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, type, name, paramType, resultType);
    }

    @Override
    public String toString() {
        return "OperatorOverloadingContext[" +
                "operator=" + operator + ", " +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "paramType=" + paramType + ", " +
                "resultType=" + resultType + ']';
    }


}
