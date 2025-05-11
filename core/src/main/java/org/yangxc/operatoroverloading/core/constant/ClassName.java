package org.yangxc.operatoroverloading.core.constant;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClassName {

    public static final String BIG_DECIMAL = "java.math.BigDecimal";
    public static final String BIG_INTEGER = "java.math.BigInteger";
    public static final String STRING = "java.lang.String";

    public static final String BOOLEAN = boolean.class.getTypeName();
    public static final String BYTE = byte.class.getTypeName();
    public static final String SHORT = short.class.getTypeName();
    public static final String INT = int.class.getTypeName();
    public static final String LONG = long.class.getTypeName();
    public static final String DOUBLE = double.class.getTypeName();
    public static final String FLOAT = float.class.getTypeName();

    public static final String BOOLEAN_BOXED = Boolean.class.getTypeName();
    public static final String BYTE_BOXED = Byte.class.getTypeName();
    public static final String SHORT_BOXED = Short.class.getTypeName();
    public static final String INT_BOXED = Integer.class.getTypeName();
    public static final String LONG_BOXED = Long.class.getTypeName();
    public static final String DOUBLE_BOXED = Double.class.getTypeName();
    public static final String FLOAT_BOXED = Float.class.getTypeName();

    public static final Set<String> PRIMITIVE_NAME = Set.of(
            BOOLEAN,
            BYTE,
            SHORT,
            INT,
            LONG,
            DOUBLE,
            FLOAT
    );

    private static final Map<String, String> UNBOX_MAP = Map.of(
            BOOLEAN_BOXED, BOOLEAN,
            BYTE_BOXED, BYTE,
            SHORT_BOXED, SHORT,
            INT_BOXED, INT,
            LONG_BOXED, LONG,
            DOUBLE_BOXED, DOUBLE,
            FLOAT_BOXED, FLOAT
    );

    public static boolean isBoxed(String boxedType) {
        return UNBOX_MAP.containsKey(boxedType);
    }

    public static String unboxedType(String boxedType) {
        return UNBOX_MAP.getOrDefault(boxedType, boxedType);
    }

    public static String getSimpleName(String name) {
        int i = name.lastIndexOf(".");
        return i == -1 ? name : name.substring(i + 1);
    }

    private static final Map<String, Integer> PRIMITIVE_LEVEL = Map.of(
            BYTE, 0,
            SHORT, 1,
            INT, 2,
            LONG, 3,
            FLOAT, 4,
            DOUBLE, 5,
            STRING, 6
    );

    public static String getPrimitiveType(String aType, String bType) {
        Integer aLevel = Objects.requireNonNull(PRIMITIVE_LEVEL.get(aType), "type [" + aType + "] not supported primitive operator");
        Integer bLevel = Objects.requireNonNull(PRIMITIVE_LEVEL.get(bType), "type [" + bType + "] not supported primitive operator");
        return aLevel > bLevel ? aType : bType;
    }

    private ClassName() {}

}
