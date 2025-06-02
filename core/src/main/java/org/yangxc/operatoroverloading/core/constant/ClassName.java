package org.yangxc.operatoroverloading.core.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static final Set<String> PRIMITIVE_NAME = Stream.of(
            BOOLEAN,
            BYTE,
            SHORT,
            INT,
            LONG,
            DOUBLE,
            FLOAT
    ).collect(Collectors.toSet());

    private static final Map<String, String> UNBOX_MAP;
    static {
        UNBOX_MAP = new HashMap<>();
        UNBOX_MAP.put(BOOLEAN_BOXED, BOOLEAN);
        UNBOX_MAP.put(BYTE_BOXED, BYTE);
        UNBOX_MAP.put(SHORT_BOXED, SHORT);
        UNBOX_MAP.put(INT_BOXED, INT);
        UNBOX_MAP.put(LONG_BOXED, LONG);
        UNBOX_MAP.put(DOUBLE_BOXED, DOUBLE);
        UNBOX_MAP.put(FLOAT_BOXED, FLOAT);
    }

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

    private static final Map<String, Integer> PRIMITIVE_LEVEL;
    static {
        PRIMITIVE_LEVEL = new HashMap<>();
        PRIMITIVE_LEVEL.put(BYTE, 0);
        PRIMITIVE_LEVEL.put(SHORT, 1);
        PRIMITIVE_LEVEL.put(INT, 2);
        PRIMITIVE_LEVEL.put(LONG, 3);
        PRIMITIVE_LEVEL.put(FLOAT, 4);
        PRIMITIVE_LEVEL.put(DOUBLE, 5);
        PRIMITIVE_LEVEL.put(STRING, 6);
    }

    public static String getPrimitiveType(String aType, String bType) {
        Integer aLevel = Objects.requireNonNull(PRIMITIVE_LEVEL.get(aType), "type [" + aType + "] not supported primitive operator");
        Integer bLevel = Objects.requireNonNull(PRIMITIVE_LEVEL.get(bType), "type [" + bType + "] not supported primitive operator");
        return aLevel > bLevel ? aType : bType;
    }

    private ClassName() {}

}
