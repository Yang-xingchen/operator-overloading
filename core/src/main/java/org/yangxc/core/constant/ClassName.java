package org.yangxc.core.constant;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassName {

    public static final String BIG_DECIMAL = "java.math.BigDecimal";

    public static final Set<String> PRIMITIVE_NAME = Stream.of(
            boolean.class, byte.class, short.class, int.class, long.class,
            double.class, float.class
    ).map(Class::getTypeName).collect(Collectors.toSet());

}
