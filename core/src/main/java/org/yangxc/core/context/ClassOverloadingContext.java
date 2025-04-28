package org.yangxc.core.context;

import org.yangxc.core.annotation.Operator;

import static org.yangxc.core.constany.ClassName.BIG_DECIMAL;

public class ClassOverloadingContext {

    private final String typeName;

    private OperatorOverloadingContext add;
    private OperatorOverloadingContext subtract;
    private OperatorOverloadingContext multiply;
    private OperatorOverloadingContext divide;
    private OperatorOverloadingContext remainder;

    private CastContext from;
    private CastContext toByte;
    private CastContext toShort;
    private CastContext toInt;
    private CastContext toLong;
    private CastContext toFloat;
    private CastContext toDouble;

    public static final ClassOverloadingContext BIG_DECIMAL_CONTEXT;

    static {
        BIG_DECIMAL_CONTEXT = new ClassOverloadingContext(BIG_DECIMAL);
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.ADD, "add", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.SUBTRACT, "subtract", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.MULTIPLY, "multiply", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.DIVIDE, "divide", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.REMAINDER, "remainder", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new CastContext("new BigDecimal", "java.lang.String", BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new CastContext("byteValueExact", BIG_DECIMAL, "byte"));
        BIG_DECIMAL_CONTEXT.set(new CastContext("shortValueExact", BIG_DECIMAL, "short"));
        BIG_DECIMAL_CONTEXT.set(new CastContext("intValueExact", BIG_DECIMAL, "int"));
        BIG_DECIMAL_CONTEXT.set(new CastContext("longValueExact", BIG_DECIMAL, "long"));
        BIG_DECIMAL_CONTEXT.set(new CastContext("floatValue", BIG_DECIMAL, "float"));
        BIG_DECIMAL_CONTEXT.set(new CastContext("doubleValue", BIG_DECIMAL, "double"));
    }

    public ClassOverloadingContext(String typeName) {
        this.typeName = typeName;
    }

    public ClassOverloadingContext set(OperatorOverloadingContext context) {
        switch (context.operator()) {
            case ADD -> add = context;
            case SUBTRACT -> subtract = context;
            case MULTIPLY -> multiply = context;
            case DIVIDE -> divide = context;
            case REMAINDER -> remainder = context;
        }
        return this;
    }

    public ClassOverloadingContext set(CastContext context) {
        if (typeName.equals(context.to())) {
            from = context;
            return this;
        }
        switch (context.to()) {
            case "byte" -> toByte = context;
            case "short" -> toShort = context;
            case "int" -> toInt = context;
            case "long" -> toLong = context;
            case "float" -> toFloat = context;
            case "double" -> toDouble = context;
        }
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public OperatorOverloadingContext getAdd() {
        return add;
    }

    public OperatorOverloadingContext getSubtract() {
        return subtract;
    }

    public OperatorOverloadingContext getMultiply() {
        return multiply;
    }

    public OperatorOverloadingContext getDivide() {
        return divide;
    }

    public OperatorOverloadingContext getRemainder() {
        return remainder;
    }

    public CastContext getFrom() {
        return from;
    }

    public CastContext getToByte() {
        return toByte;
    }

    public CastContext getToShort() {
        return toShort;
    }

    public CastContext getToInt() {
        return toInt;
    }

    public CastContext getToLong() {
        return toLong;
    }

    public CastContext getToFloat() {
        return toFloat;
    }

    public CastContext getToDouble() {
        return toDouble;
    }
}
