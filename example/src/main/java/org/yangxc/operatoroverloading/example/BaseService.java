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
     * set field
     */
    @ServiceField
    void setTwo(BigDecimal two);

    /**
     * number add
     * @return
     */
    @ServiceFunction("BigDecimal.ONE+two-3+4")
    int add();

    @ServiceFunction("this.two + two")
    int add(BigDecimal two);

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

    /**
     * local var
     */
    @ServiceFunction(
            statements = {
                    @Statement(type = BigDecimal.class, varName = "a", exp = "1.23*2"),
                    @Statement(type = BigDecimal.class, varName = "b", exp = "3/4")
            },
            value = "a+b"
    )
    double var();

    @ServiceFunction(value = "123_456_789_123_456_789 % 1_000_000_000", numberType = NumberType.BIG_INTEGER)
    long big();

    @ServiceFunction(value = "(BigDecimal)a + a + (BigDecimal)b", doc = DocType.EXP)
    BigDecimal castAdd(BigDecimal a, int b);

    /**
     * cast
     * @param a
     * @param b
     * @return
     */
    @ServiceFunction(value = "(BigDecimal)((int)a + b) + a", doc = DocType.EXP)
    int castAdd1(BigDecimal a, int b);

    @ServiceFunction(value = "(1+2)*3", doc = DocType.DOC)
    int parenthesis();

    /**
     * primitive operator
     */
    @ServiceFunction(value = "1.23+4e2", numberType = NumberType.PRIMITIVE, doc = DocType.DOC)
    double primitive();

    @ServiceFunction(".23+.0045")
    BigDecimal ignoreInteger();

    @ServiceFunction("-12.34e2-.56-.78e-2")
    BigDecimal e();

    @ServiceFunction(value = "a!=0?a:b", pares = false)
    int custom(int a, int b);

}
