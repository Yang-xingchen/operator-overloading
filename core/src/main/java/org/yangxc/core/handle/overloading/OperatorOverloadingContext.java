package org.yangxc.core.handle.overloading;

import org.yangxc.core.annotation.Operator;
import org.yangxc.core.constant.OperatorType;

import java.util.Objects;

public final class OperatorOverloadingContext {

    private final Operator operator;
    private final OperatorType type;
    private final String name;
    private final String paramType;
    private final String resultType;

    public OperatorOverloadingContext(Operator operator, OperatorType type, String name, String paramType, String resultType) {
        this.operator = operator;
        this.type = type;
        this.name = name;
        this.paramType = paramType;
        this.resultType = resultType;
    }

    public Operator operator() {
        return operator;
    }

    public OperatorType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String paramType() {
        return paramType;
    }

    public String resultType() {
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
