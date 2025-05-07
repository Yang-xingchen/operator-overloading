package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.annotation.OperatorType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassOverloadingContext {

    private final String typeName;

    private Map<OperatorType, OperatorOverloadingContext> operatorMap = new HashMap<>();
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

    public Set<OperatorType> supportOperator() {
        return operatorMap.keySet();
    }

    public Set<String> supportCast() {
        return castToMap.keySet();
    }

    public OperatorOverloadingContext operatorOverloading(OperatorType operator) {
        if (operatorMap.containsKey(operator)) {
            return operatorMap.get(operator);
        }
        throw new UnsupportedOperationException("unsupported operator[" + operator.symbol + "]: " + typeName);
    }

    public OperatorOverloadingContext getAdd() {
        return operatorOverloading(OperatorType.ADD);
    }

    public OperatorOverloadingContext getSubtract() {
        return operatorOverloading(OperatorType.SUBTRACT);
    }

    public OperatorOverloadingContext getMultiply() {
        return operatorOverloading(OperatorType.MULTIPLY);
    }

    public OperatorOverloadingContext getDivide() {
        return operatorOverloading(OperatorType.DIVIDE);
    }

    public OperatorOverloadingContext getRemainder() {
        return operatorOverloading(OperatorType.REMAINDER);
    }

    public CastContext cast(String toTypeName) {
        if (castToMap.containsKey(toTypeName)) {
            return castToMap.get(toTypeName);
        }
        throw new UnsupportedOperationException("unsupported cast[" + toTypeName + "]: " + typeName);
    }

}
