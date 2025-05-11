package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.*;

import java.math.BigDecimal;

/**
 * 测试变量
 */
@OperatorService(imports = {VarService.class, BigDecimal.class})
public interface VarService {

    @OperatorClassConst
    BigDecimal var = new BigDecimal("1");

    @ServiceField
    void setVar(BigDecimal var);

    @ServiceFunction("BigDecimal.ONE")
    BigDecimal inner();

    @ServiceFunction("VarService.var")
    BigDecimal staticVar();

    @ServiceFunction("this.var")
    BigDecimal fieldVar();

    @ServiceFunction("var")
    BigDecimal ignoreThis();

    @ServiceFunction(
            statements = @Statement(type = BigDecimal.class, varName = "var", exp = "3"),
            value = "var"
    )
    BigDecimal statementVar();

    @ServiceFunction("var")
    BigDecimal paramVar(BigDecimal var);

    @ServiceFunction(
            statements = {
                    @Statement(type = BigDecimal.class, varName = "var", exp = "1.23*2"),
            },
            value = "VarService.var + this.var + var"
    )
    double var();

    @ServiceFunction("VarService.var + this.var + var")
    double var(BigDecimal var);

}
