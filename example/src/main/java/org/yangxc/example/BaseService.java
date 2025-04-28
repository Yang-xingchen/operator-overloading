package org.yangxc.example;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.annotation.OperatorService;

import java.math.BigDecimal;

@OperatorService
public interface BaseService {

    @OperatorFunction("1+2-3+4")
    int add();

    @OperatorFunction("a+b")
    int add(BigDecimal a, BigDecimal b);

    @OperatorFunction("1.23*a+3/4")
    BigDecimal multiply(BigDecimal a);

}
