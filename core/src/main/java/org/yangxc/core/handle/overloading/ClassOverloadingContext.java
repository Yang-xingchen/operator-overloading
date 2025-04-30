package org.yangxc.core.handle.overloading;

import org.yangxc.core.annotation.Operator;

import java.util.HashMap;
import java.util.Map;

public class ClassOverloadingContext {

    private final String typeName;

    private Map<Operator, OperatorOverloadingContext> operatorMap = new HashMap<>();
    private Map<String, CastContext> castToMap = new HashMap<>();

    public ClassOverloadingContext(String typeName) {
        this.typeName = typeName;
    }

    public ClassOverloadingContext set(OperatorOverloadingContext context) {
        operatorMap.put(context.operator(), context);
        return this;
    }

    public ClassOverloadingContext set(CastContext context) {
        castToMap.put(context.to(), context);
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public OperatorOverloadingContext operatorOverloading(Operator operator) {
        if (operatorMap.containsKey(operator)) {
            return operatorMap.get(operator);
        }
        throw new UnsupportedOperationException("unsupported operator[" + operator.symbol + "]: " + typeName);
    }

    public OperatorOverloadingContext getAdd() {
        return operatorOverloading(Operator.ADD);
    }

    public OperatorOverloadingContext getSubtract() {
        return operatorOverloading(Operator.SUBTRACT);
    }

    public OperatorOverloadingContext getMultiply() {
        return operatorOverloading(Operator.MULTIPLY);
    }

    public OperatorOverloadingContext getDivide() {
        return operatorOverloading(Operator.DIVIDE);
    }

    public OperatorOverloadingContext getRemainder() {
        return operatorOverloading(Operator.REMAINDER);
    }

    public CastContext cast(String toTypeName) {
        if (castToMap.containsKey(toTypeName)) {
            return castToMap.get(toTypeName);
        }
        throw new UnsupportedOperationException("unsupported cast[" + toTypeName + "]: " + typeName);
    }

}
