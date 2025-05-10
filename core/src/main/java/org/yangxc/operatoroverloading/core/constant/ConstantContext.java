package org.yangxc.operatoroverloading.core.constant;

import org.yangxc.operatoroverloading.core.handle.service.VariableContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstantContext {

    public static final VariableContext BIG_DECIMAL_ZERO = VariableContext.createByStatic(ClassName.BIG_DECIMAL, ClassName.BIG_DECIMAL, "ZERO");
    public static final VariableContext BIG_DECIMAL_ONE = VariableContext.createByStatic(ClassName.BIG_DECIMAL, ClassName.BIG_DECIMAL, "ONE");
    public static final VariableContext BIG_DECIMAL_TWO = VariableContext.createByStatic(ClassName.BIG_DECIMAL, ClassName.BIG_DECIMAL, "TWO");
    public static final VariableContext BIG_DECIMAL_TEN = VariableContext.createByStatic(ClassName.BIG_DECIMAL, ClassName.BIG_DECIMAL, "TEN");

    public static final VariableContext BIG_INTEGER_ONE = VariableContext.createByStatic(ClassName.BIG_INTEGER, ClassName.BIG_INTEGER, "ONE");
    public static final VariableContext BIG_INTEGER_TWO = VariableContext.createByStatic(ClassName.BIG_INTEGER, ClassName.BIG_INTEGER, "TWO");
    public static final VariableContext BIG_INTEGER_NEGATIVE_ONE = VariableContext.createByStatic(ClassName.BIG_INTEGER, ClassName.BIG_INTEGER, "NEGATIVE_ONE");
    public static final VariableContext BIG_INTEGER_TEN = VariableContext.createByStatic(ClassName.BIG_INTEGER, ClassName.BIG_INTEGER, "TEN");

    public static final Map<String, VariableContext> DEFAULT_CONSTANT = Stream.of(
            BIG_DECIMAL_ZERO,
            BIG_DECIMAL_ONE,
            BIG_DECIMAL_TWO,
            BIG_DECIMAL_TEN,
            BIG_INTEGER_ONE,
            BIG_INTEGER_TWO,
            BIG_INTEGER_NEGATIVE_ONE,
            BIG_INTEGER_TEN
    ).collect(Collectors.toMap(VariableContext::getQualifiedName, Function.identity()));

}
