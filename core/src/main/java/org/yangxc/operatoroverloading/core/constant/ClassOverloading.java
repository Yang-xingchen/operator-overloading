package org.yangxc.operatoroverloading.core.constant;

import org.yangxc.operatoroverloading.core.annotation.OperatorType;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OperatorOverloadingContext;

import java.util.HashMap;
import java.util.Map;

import static org.yangxc.operatoroverloading.core.constant.CastMethodType.*;
import static org.yangxc.operatoroverloading.core.constant.ClassName.*;

public class ClassOverloading {

    public static final ClassOverloadingContext BIG_DECIMAL_CONTEXT;

    static {
        BIG_DECIMAL_CONTEXT = new ClassOverloadingContext(BIG_DECIMAL);
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.METHOD, null, "add", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.METHOD, null, "subtract", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.METHOD, null, "multiply", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.METHOD, null, "divide", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.METHOD, null, "remainder", BIG_DECIMAL, BIG_DECIMAL));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "byteValueExact", BYTE));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "shortValueExact", SHORT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "intValueExact", INT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "longValueExact", LONG));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "floatValue", FLOAT));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "doubleValue", DOUBLE));
        BIG_DECIMAL_CONTEXT.set(new CastContext(BIG_DECIMAL, METHOD, null, "toString", STRING));
    }


    public static final ClassOverloadingContext BIG_INTEGER_CONTEXT;

    static {
        BIG_INTEGER_CONTEXT = new ClassOverloadingContext(BIG_INTEGER);
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.METHOD, null, "add", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.METHOD, null, "subtract", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.METHOD, null, "multiply", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.METHOD, null, "divide", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.METHOD, null, "remainder", BIG_INTEGER, BIG_INTEGER));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "byteValueExact", BYTE));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "shortValueExact", SHORT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "intValueExact", INT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "longValueExact", LONG));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "floatValue", FLOAT));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "doubleValue", DOUBLE));
        BIG_INTEGER_CONTEXT.set(new CastContext(BIG_INTEGER, METHOD, null, "toString", STRING));
    }


    public static final ClassOverloadingContext STRING_CONTEXT;

    static {
        STRING_CONTEXT = new ClassOverloadingContext(STRING);
        STRING_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.METHOD, null, "concat", STRING, STRING));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Byte", "parseByte", BYTE));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Short", "parseShort", SHORT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Integer", "parseInt", INT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Long", "parseLong", LONG));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Float", "parseFloat", FLOAT));
        STRING_CONTEXT.set(new CastContext(STRING, STATIC_METHOD, "java.lang.Double", "parseDouble", DOUBLE));
        STRING_CONTEXT.set(new CastContext(STRING, NEW, null, null, BIG_DECIMAL));
        STRING_CONTEXT.set(new CastContext(STRING, NEW, null, null, BIG_INTEGER));
    }


    public static final ClassOverloadingContext BYTE_CONTEXT;

    static {
        BYTE_CONTEXT = new ClassOverloadingContext(BYTE);
        BYTE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", BYTE, BYTE));
        BYTE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", BYTE, BYTE));
        BYTE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", BYTE, BYTE));
        BYTE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", BYTE, BYTE));
        BYTE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", BYTE, BYTE));
        BYTE_CONTEXT.set(new CastContext(BYTE, STATIC_METHOD, "java.lang.Byte", "toString", STRING));
        BYTE_CONTEXT.set(new CastContext(BYTE, NEW, null, null, BIG_DECIMAL));
        BYTE_CONTEXT.set(new CastContext(BYTE, STATIC_METHOD, BIG_INTEGER, "valueOf", BIG_INTEGER));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, BYTE));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, SHORT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, INT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, LONG));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, FLOAT));
        BYTE_CONTEXT.set(new CastContext(BYTE, CAST, null, null, DOUBLE));
    }


    public static final ClassOverloadingContext SHORT_CONTEXT;

    static {
        SHORT_CONTEXT = new ClassOverloadingContext(SHORT);
        SHORT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", SHORT, SHORT));
        SHORT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", SHORT, SHORT));
        SHORT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", SHORT, SHORT));
        SHORT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", SHORT, SHORT));
        SHORT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", SHORT, SHORT));
        SHORT_CONTEXT.set(new CastContext(SHORT, STATIC_METHOD, "java.lang.Short", "toString", STRING));
        SHORT_CONTEXT.set(new CastContext(SHORT, NEW, null, null, BIG_DECIMAL));
        SHORT_CONTEXT.set(new CastContext(SHORT, STATIC_METHOD, BIG_INTEGER, "valueOf", BIG_INTEGER));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, BYTE));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, SHORT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, INT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, LONG));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, FLOAT));
        SHORT_CONTEXT.set(new CastContext(SHORT, CAST, null, null, DOUBLE));
    }


    public static final ClassOverloadingContext INT_CONTEXT;

    static {
        INT_CONTEXT = new ClassOverloadingContext(INT);
        INT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", INT, INT));
        INT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", INT, INT));
        INT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", INT, INT));
        INT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", INT, INT));
        INT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", INT, INT));
        INT_CONTEXT.set(new CastContext(INT, STATIC_METHOD, "java.lang.Int", "toString", STRING));
        INT_CONTEXT.set(new CastContext(INT, NEW, null, null, BIG_DECIMAL));
        INT_CONTEXT.set(new CastContext(INT, STATIC_METHOD, BIG_INTEGER, "valueOf", BIG_INTEGER));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, BYTE));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, SHORT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, INT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, LONG));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, FLOAT));
        INT_CONTEXT.set(new CastContext(INT, CAST, null, null, DOUBLE));
    }


    public static final ClassOverloadingContext LONG_CONTEXT;

    static {
        LONG_CONTEXT = new ClassOverloadingContext(LONG);
        LONG_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", LONG, LONG));
        LONG_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", LONG, LONG));
        LONG_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", LONG, LONG));
        LONG_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", LONG, LONG));
        LONG_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", LONG, LONG));
        LONG_CONTEXT.set(new CastContext(LONG, STATIC_METHOD, "java.lang.Long", "toString", STRING));
        LONG_CONTEXT.set(new CastContext(LONG, NEW, null, null, BIG_DECIMAL));
        LONG_CONTEXT.set(new CastContext(LONG, STATIC_METHOD, BIG_INTEGER, "valueOf", BIG_INTEGER));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, BYTE));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, SHORT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, INT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, LONG));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, FLOAT));
        LONG_CONTEXT.set(new CastContext(LONG, CAST, null, null, DOUBLE));
    }


    public static final ClassOverloadingContext FLOAT_CONTEXT;

    static {
        FLOAT_CONTEXT = new ClassOverloadingContext(FLOAT);
        FLOAT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", FLOAT, FLOAT));
        FLOAT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", FLOAT, FLOAT));
        FLOAT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", FLOAT, FLOAT));
        FLOAT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", FLOAT, FLOAT));
        FLOAT_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", FLOAT, FLOAT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, STATIC_METHOD, "java.lang.Float", "toString", STRING));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, NEW, null, null, BIG_DECIMAL));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, BYTE));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, SHORT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, INT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, LONG));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, FLOAT));
        FLOAT_CONTEXT.set(new CastContext(FLOAT, CAST, null, null, DOUBLE));
    }


    public static final ClassOverloadingContext DOUBLE_CONTEXT;

    static {
        DOUBLE_CONTEXT = new ClassOverloadingContext(DOUBLE);
        DOUBLE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.ADD, OperatorMethodType.PRIMITIVE, null, "+", DOUBLE, DOUBLE));
        DOUBLE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.SUBTRACT, OperatorMethodType.PRIMITIVE, null, "-", DOUBLE, DOUBLE));
        DOUBLE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.MULTIPLY, OperatorMethodType.PRIMITIVE, null, "*", DOUBLE, DOUBLE));
        DOUBLE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.DIVIDE, OperatorMethodType.PRIMITIVE, null, "/", DOUBLE, DOUBLE));
        DOUBLE_CONTEXT.set(new OperatorOverloadingContext(OperatorType.REMAINDER, OperatorMethodType.PRIMITIVE, null, "%", DOUBLE, DOUBLE));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, STATIC_METHOD, "java.lang.Double", "toString", STRING));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, NEW, null, null, BIG_DECIMAL));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, BYTE));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, SHORT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, INT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, LONG));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, FLOAT));
        DOUBLE_CONTEXT.set(new CastContext(DOUBLE, CAST, null, null, DOUBLE));
    }

    public static final Map<String, ClassOverloadingContext> DEFAULT_CLASS_OVERLOADING;
    static {
        DEFAULT_CLASS_OVERLOADING = new HashMap<>();
        DEFAULT_CLASS_OVERLOADING.put(BIG_DECIMAL, BIG_DECIMAL_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(BIG_INTEGER, BIG_INTEGER_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(STRING, STRING_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(BYTE, BYTE_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(SHORT, SHORT_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(INT, INT_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(LONG, LONG_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(FLOAT, FLOAT_CONTEXT);
        DEFAULT_CLASS_OVERLOADING.put(DOUBLE, DOUBLE_CONTEXT);
    }

    private ClassOverloading() {
    }

}
