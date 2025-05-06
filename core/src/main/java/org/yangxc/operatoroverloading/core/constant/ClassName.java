package org.yangxc.operatoroverloading.core.constant;

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

    public static final Set<String> PRIMITIVE_NAME = Set.of(
            BOOLEAN,
            BYTE,
            SHORT,
            INT,
            LONG,
            DOUBLE,
            FLOAT
    );

    public static String getSimpleName(String name) {
        int i = name.lastIndexOf(".");
        return i == -1 ? name : name.substring(i + 1);
    }

    private ClassName() {}

}
