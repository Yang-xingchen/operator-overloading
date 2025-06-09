package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.*;

import java.math.BigDecimal;

/**
 * this test service
 *
 * @author yxc
 */
@OperatorService(imports = {BigDecimal.class, String.class}, doc = DocType.DOC_EXP)
public interface CastService {

    @ServiceFunction(value = "1", numberType = NumberType.BIG_DECIMAL)
    BigDecimal notCast();

    @ServiceFunction(value = "1", numberType = NumberType.BIG_DECIMAL)
    int bdToInt();

    @ServiceFunction(value = "(BigDecimal)i")
    BigDecimal intToBd(int i);

    @ServiceFunction(value = "(String)(BigDecimal)(int)s")
    double multiCast(String s);

    @ServiceFunction(value = "(BigDecimal)a + a + (BigDecimal)b", doc = DocType.EXP)
    BigDecimal add(BigDecimal a, int b);

    @ServiceFunction(value = "(BigDecimal)((int)a + b) + a", doc = DocType.EXP)
    int complex(BigDecimal a, int b);

}
