package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.*;

import java.math.BigDecimal;

/**
 * this test service
 *
 * @author yxc
 */
@OperatorService(imports = BigDecimal.class, doc = DocType.DOC_EXP)
public interface BaseService {

    /**
     * number add
     * @return
     */
    @ServiceFunction("BigDecimal.ONE+2-3+4")
    int add();

    /**
     * var add
     * @param a
     * @param b
     * @return
     */
    @ServiceFunction("a+b")
    int add(BigDecimal a, BigDecimal b);

    /**
     * multiply
     */
    @ServiceFunction("1.23*a+3/4")
    BigDecimal multiply(BigDecimal a);

    @ServiceFunction(value = "(1+2)*3", doc = DocType.DOC)
    int parenthesis();

    @ServiceFunction(value = "123_456_789_123_456_789 % 1_000_000_000", numberType = NumberType.BIG_INTEGER)
    long big();

    @ServiceFunction(value = "a!=0?a:b", pares = false)
    int custom(int a, int b);

}
