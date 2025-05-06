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
    @OperatorFunction("1+2-3+4")
    int add();

    /**
     * var add
     * @param a
     * @param b
     * @return
     */
    @OperatorFunction("a+b")
    int add(BigDecimal a, BigDecimal b);

    /**
     * multiply
     */
    @OperatorFunction("1.23*a+3/4")
    BigDecimal multiply(BigDecimal a);

    /**
     * local var
     */
    @OperatorFunction(
            statements = {
                    @Statement(type = BigDecimal.class, varName = "a", exp = "1.23*2"),
                    @Statement(type = BigDecimal.class, varName = "b", exp = "3/4")
            },
            value = "a+b"
    )
    double var();

    @OperatorFunction(value = "123_456_789_123_456_789 % 1_000_000_000", numberType = NumberType.BIG_INTEGER)
    long big();

    @OperatorFunction(value = "(BigDecimal)a + a + (BigDecimal)b", doc = DocType.EXP)
    BigDecimal castAdd(BigDecimal a, int b);

    /**
     * cast
     * @param a
     * @param b
     * @return
     */
    @OperatorFunction(value = "(BigDecimal)((int)a + b) + a", doc = DocType.EXP)
    int castAdd1(BigDecimal a, int b);

    @OperatorFunction(value = "(1+2)*3", doc = DocType.DOC)
    int parenthesis();

    /**
     * primitive operator
     */
    @OperatorFunction(value = "1.23+4e2", numberType = NumberType.PRIMITIVE, doc = DocType.DOC)
    double primitive();

    @OperatorFunction(".23+.0045")
    BigDecimal ignoreInteger();

    @OperatorFunction("-12.34e2-.56-.78e-2")
    BigDecimal e();

    @OperatorFunction(value = "a!=0?a:b", pares = false)
    int custom(int a, int b);

}
