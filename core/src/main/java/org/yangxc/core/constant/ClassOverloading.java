package org.yangxc.core.constant;

import org.yangxc.core.annotation.Operator;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.core.handle.overloading.OperatorOverloadingContext;

import java.util.Map;

import static org.yangxc.core.constant.CastType.*;
import static org.yangxc.core.constant.ClassName.*;

public class ClassOverloading {

    public static final ClassOverloadingContext BIG_DECIMAL_CONTEXT;

    static {
        BIG_DECIMAL_CONTEXT = new ClassOverloadingContext(BIG_DECIMAL);
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.ADD, "add", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.SUBTRACT, "subtract", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.MULTIPLY, "multiply", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.DIVIDE, "divide", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(Operator.REMAINDER, "remainder", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "byteValueExact", BYTE));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "shortValueExact", SHORT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "intValueExact", INT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "longValueExact", LONG));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "floatValue", FLOAT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "doubleValue", DOUBLE));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, "toString", STRING));
    }


    public static final ClassOverloadingContext BIG_INTEGER_CONTEXT;

    static {
        BIG_INTEGER_CONTEXT = new ClassOverloadingContext(BIG_INTEGER);
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(Operator.ADD, "add", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(Operator.SUBTRACT, "subtract", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(Operator.MULTIPLY, "multiply", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(Operator.DIVIDE, "divide", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(Operator.REMAINDER, "remainder", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "byteValueExact", BYTE));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "shortValueExact", SHORT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "intValueExact", INT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "longValueExact", LONG));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "floatValue", FLOAT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "doubleValue", DOUBLE));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, "toString", STRING));
    }


    public static final ClassOverloadingContext STRING_CONTEXT;

    static {
        STRING_CONTEXT = new ClassOverloadingContext(STRING);
        STRING_CONTEXT.set(new OperatorOverloadingContext(Operator.ADD, "concat", STRING, STRING));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Byte.parseByte", BYTE));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Short.parseShort", SHORT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Integer.parseInt", INT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Long.parseLong", LONG));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Float.parseFloat", FLOAT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "Double.parseDouble", DOUBLE));
        STRING_CONTEXT.set(new CastContext(STRING, NEW, null, BIG_DECIMAL));
        STRING_CONTEXT.set(new CastContext(STRING, NEW, null, BIG_INTEGER));
    }


    public static final ClassOverloadingContext BYTE_CONTEXT;

    static {
        BYTE_CONTEXT = new ClassOverloadingContext(BYTE);
        BYTE_CONTEXT.set(new CastContext(BYTE, STATIC_METHOD, "Byte.toString", STRING));
        BYTE_CONTEXT.set(new CastContext(BYTE, NEW, null, BIG_DECIMAL));
        BYTE_CONTEXT.set(new CastContext(BYTE, STATIC_METHOD, "BigInteger.valueOf", BIG_INTEGER));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, BYTE));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, SHORT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, INT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, LONG));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, FLOAT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, DOUBLE));
    }


    public static final ClassOverloadingContext SHORT_CONTEXT;

    static {
        SHORT_CONTEXT = new ClassOverloadingContext(SHORT);
        SHORT_CONTEXT.set(new CastContext(SHORT, STATIC_METHOD, "Short.toString", STRING));
        SHORT_CONTEXT.set(new CastContext(SHORT, NEW, null, BIG_DECIMAL));
        SHORT_CONTEXT.set(new CastContext(SHORT, STATIC_METHOD, "BigInteger.valueOf", BIG_INTEGER));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, BYTE));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, SHORT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, INT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, LONG));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, FLOAT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, DOUBLE));
    }


    public static final ClassOverloadingContext INT_CONTEXT;

    static {
        INT_CONTEXT = new ClassOverloadingContext(INT);
        INT_CONTEXT.set(new CastContext(INT, STATIC_METHOD, "Int.toString", STRING));
        INT_CONTEXT.set(new CastContext(INT, NEW, null, BIG_DECIMAL));
        INT_CONTEXT.set(new CastContext(INT, STATIC_METHOD, "BigInteger.valueOf", BIG_INTEGER));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, BYTE));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, SHORT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, INT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, LONG));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, FLOAT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, DOUBLE));
    }


    public static final ClassOverloadingContext LONG_CONTEXT;

    static {
        LONG_CONTEXT = new ClassOverloadingContext(LONG);
        LONG_CONTEXT.set(new CastContext(LONG, STATIC_METHOD, "Long.toString", STRING));
        LONG_CONTEXT.set(new CastContext(LONG, NEW, null, BIG_DECIMAL));
        LONG_CONTEXT.set(new CastContext(LONG, STATIC_METHOD, "BigInteger.valueOf", BIG_INTEGER));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, BYTE));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, SHORT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, INT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, LONG));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, FLOAT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, DOUBLE));
    }


    public static final ClassOverloadingContext FLOAT_CONTEXT;

    static {
        FLOAT_CONTEXT = new ClassOverloadingContext(FLOAT);
        FLOAT_CONTEXT.set(new CastContext(FLOAT, STATIC_METHOD, "Float.toString", STRING));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, NEW, null, BIG_DECIMAL));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, BYTE));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, SHORT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, INT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, LONG));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, FLOAT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, DOUBLE));
    }


    public static final ClassOverloadingContext DOUBLE_CONTEXT;

    static {
        DOUBLE_CONTEXT = new ClassOverloadingContext(DOUBLE);
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, STATIC_METHOD, "Double.toString", STRING));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, NEW, null, BIG_DECIMAL));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, BYTE));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, SHORT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, INT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, LONG));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, FLOAT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, DOUBLE));
    }

    public static final Map<String, ClassOverloadingContext> DEFAULT_CLASS_OVERLOADING = Map.of(
            BIG_DECIMAL, BIG_DECIMAL_CONTEXT,
            BIG_INTEGER, BIG_INTEGER_CONTEXT,
            STRING, STRING_CONTEXT,
            BYTE, BYTE_CONTEXT,
            SHORT, SHORT_CONTEXT,
            INT, INT_CONTEXT,
            LONG, LONG_CONTEXT,
            FLOAT, FLOAT_CONTEXT,
            DOUBLE, DOUBLE_CONTEXT
    );

    private ClassOverloading() {
    }

}
