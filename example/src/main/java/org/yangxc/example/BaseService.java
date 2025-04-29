package org.yangxc.example;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.annotation.OperatorService;
import org.yangxc.core.annotation.Statement;

import java.math.BigDecimal;

@OperatorService
public interface BaseService {

    @OperatorFunction("1+2-3+4")
    int add();

    @OperatorFunction("a+b")
    int add(BigDecimal a, BigDecimal b);


    @OperatorFunction("1.23*a+3/4")
    BigDecimal multiply(BigDecimal a);

    @OperatorFunction(
            statements = {
                    @Statement(type = BigDecimal.class, varName = "a", exp = "1.23*2"),
                    @Statement(type = BigDecimal.class, varName = "b", exp = "3/4")
            },
            value = "a+b"
    )
    double var();


}
